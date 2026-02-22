#include "Unreal2DToolkit.h"
#include "Modules/ModuleManager.h"
#include "LevelEditor.h"
#include "ToolMenus.h"
#include "Framework/Commands/Commands.h"
#include "Framework/Commands/UICommandList.h"
#include "Widgets/SWindow.h"
#include "Widgets/Text/STextBlock.h"
#include "Widgets/Docking/SDockTab.h"
#include "Widgets/Layout/SBox.h"
#include "Widgets/Input/SButton.h"
#include "EditorStyleSet.h"
#include "Framework/Docking/TabManager.h"
#include "Misc/Paths.h"
#include "HAL/PlatformProcess.h"

#include "Unreal2DToolkitCommands.h"
#include "Unreal2DToolkitWidgets.h"

#if WITH_EDITOR
#include "Editor.h"
#endif

#define LOCTEXT_NAMESPACE "FUnreal2DToolkitModule"

TSharedPtr<FUICommandList> PluginCommands;

// Tab names
const FName FUnreal2DToolkitModule::ToolkitTabName = FName("Unreal2DToolkit_Toolkit");
const FName FUnreal2DToolkitModule::SpriteSlicerTabName = FName("Unreal2DToolkit_SpriteSlicer");
const FName FUnreal2DToolkitModule::FlipbookGeneratorTabName = FName("Unreal2DToolkit_FlipbookGenerator");
const FName FUnreal2DToolkitModule::TilePaletteTabName = FName("Unreal2DToolkit_TilePalette");
const FName FUnreal2DToolkitModule::AtlasPackerTabName = FName("Unreal2DToolkit_AtlasPacker");

void FUnreal2DToolkitModule::StartupModule()
{
    // Register commands
    FUnreal2DToolkitCommands::Register();
    PluginCommands = MakeShareable(new FUICommandList);

    const FUnreal2DToolkitCommands& Commands = FUnreal2DToolkitCommands::Get();

    PluginCommands->MapAction(
        Commands.OpenToolkit,
        FExecuteAction::CreateRaw(this, &FUnreal2DToolkitModule::OpenToolkit)
    );

    PluginCommands->MapAction(
        Commands.SpriteSlicer,
        FExecuteAction::CreateRaw(this, &FUnreal2DToolkitModule::OpenSpriteSlicer)
    );

    PluginCommands->MapAction(
        Commands.FlipbookGenerator,
        FExecuteAction::CreateRaw(this, &FUnreal2DToolkitModule::OpenFlipbookGenerator)
    );

    PluginCommands->MapAction(
        Commands.TilePalette,
        FExecuteAction::CreateRaw(this, &FUnreal2DToolkitModule::OpenTilePalette)
    );

    PluginCommands->MapAction(
        Commands.AtlasPacker,
        FExecuteAction::CreateRaw(this, &FUnreal2DToolkitModule::OpenAtlasPacker)
    );

    PluginCommands->MapAction(
        Commands.OpenFlipbookSpec,
        FExecuteAction::CreateRaw(this, &FUnreal2DToolkitModule::OpenFlipbookSpec)
    );

    PluginCommands->MapAction(
        Commands.OpenAllTabs,
        FExecuteAction::CreateRaw(this, &FUnreal2DToolkitModule::OpenAllTabs)
    );

    PluginCommands->MapAction(
        Commands.RestoreLayout,
        FExecuteAction::CreateRaw(this, &FUnreal2DToolkitModule::RestoreLayout)
    );

    // Register menu entries
    RegisterMenus();

    // Register tab spawners
    FGlobalTabmanager::Get()->RegisterNomadTabSpawner(ToolkitTabName, FOnSpawnTab::CreateRaw(this, &FUnreal2DToolkitModule::SpawnToolkitTab))
        .SetDisplayName(LOCTEXT("ToolkitTabTitle", "2D Toolkit"))
        .SetMenuType(ETabSpawnerMenuType::Hidden);

    FGlobalTabmanager::Get()->RegisterNomadTabSpawner(SpriteSlicerTabName, FOnSpawnTab::CreateRaw(this, &FUnreal2DToolkitModule::SpawnSpriteSlicerTab))
        .SetDisplayName(LOCTEXT("SpriteSlicerTabTitle", "Sprite Slicer"))
        .SetMenuType(ETabSpawnerMenuType::Hidden);

    FGlobalTabmanager::Get()->RegisterNomadTabSpawner(FlipbookGeneratorTabName, FOnSpawnTab::CreateRaw(this, &FUnreal2DToolkitModule::SpawnFlipbookGeneratorTab))
        .SetDisplayName(LOCTEXT("FlipbookGeneratorTabTitle", "Flipbook Generator"))
        .SetMenuType(ETabSpawnerMenuType::Hidden);

    FGlobalTabmanager::Get()->RegisterNomadTabSpawner(TilePaletteTabName, FOnSpawnTab::CreateRaw(this, &FUnreal2DToolkitModule::SpawnTilePaletteTab))
        .SetDisplayName(LOCTEXT("TilePaletteTabTitle", "Tile Palette"))
        .SetMenuType(ETabSpawnerMenuType::Hidden);

    FGlobalTabmanager::Get()->RegisterNomadTabSpawner(AtlasPackerTabName, FOnSpawnTab::CreateRaw(this, &FUnreal2DToolkitModule::SpawnAtlasPackerTab))
        .SetDisplayName(LOCTEXT("AtlasPackerTabTitle", "Atlas Packer"))
        .SetMenuType(ETabSpawnerMenuType::Hidden);
}

void FUnreal2DToolkitModule::ShutdownModule()
{
    // Unregister tab spawners
    if (FGlobalTabmanager::Get().IsValid())
    {
        FGlobalTabmanager::Get()->UnregisterNomadTabSpawner(ToolkitTabName);
        FGlobalTabmanager::Get()->UnregisterNomadTabSpawner(SpriteSlicerTabName);
        FGlobalTabmanager::Get()->UnregisterNomadTabSpawner(FlipbookGeneratorTabName);
        FGlobalTabmanager::Get()->UnregisterNomadTabSpawner(TilePaletteTabName);
        FGlobalTabmanager::Get()->UnregisterNomadTabSpawner(AtlasPackerTabName);
    }

    UnregisterMenus();
    FUnreal2DToolkitCommands::Unregister();
}

void FUnreal2DToolkitModule::RegisterMenus()
{
    if (!UToolMenus::IsMenuServiceAvailable())
    {
        return;
    }

    UToolMenus* ToolMenus = UToolMenus::Get();
    FToolMenuOwnerScoped OwnerScoped(this);

    UToolMenu* Menu = ToolMenus->ExtendMenu("LevelEditor.MainMenu.Tools");
    FToolMenuSection& Section = Menu->AddSection("Unreal2DToolkit", LOCTEXT("Unreal2DToolkitSection","2D Toolkit"));

    const FUnreal2DToolkitCommands& Commands = FUnreal2DToolkitCommands::Get();

    Section.AddMenuEntry("Unreal2DToolkit.OpenToolkit", Commands.OpenToolkit->GetLabel(), Commands.OpenToolkit->GetDescription(), FSlateIcon());
    Section.AddMenuEntry("Unreal2DToolkit.SpriteSlicer", Commands.SpriteSlicer->GetLabel(), Commands.SpriteSlicer->GetDescription(), FSlateIcon());
    Section.AddMenuEntry("Unreal2DToolkit.FlipbookGenerator", Commands.FlipbookGenerator->GetLabel(), Commands.FlipbookGenerator->GetDescription(), FSlateIcon());
    Section.AddMenuEntry("Unreal2DToolkit.TilePalette", Commands.TilePalette->GetLabel(), Commands.TilePalette->GetDescription(), FSlateIcon());
    Section.AddMenuEntry("Unreal2DToolkit.AtlasPacker", Commands.AtlasPacker->GetLabel(), Commands.AtlasPacker->GetDescription(), FSlateIcon());
    Section.AddMenuEntry("Unreal2DToolkit.OpenFlipbookSpec", Commands.OpenFlipbookSpec->GetLabel(), Commands.OpenFlipbookSpec->GetDescription(), FSlateIcon());
    Section.AddMenuEntry("Unreal2DToolkit.OpenAllTabs", Commands.OpenAllTabs->GetLabel(), Commands.OpenAllTabs->GetDescription(), FSlateIcon());
    Section.AddMenuEntry("Unreal2DToolkit.RestoreLayout", Commands.RestoreLayout->GetLabel(), Commands.RestoreLayout->GetDescription(), FSlateIcon());

    // Bind the command list to the menu
    Menu->SetContext(PluginCommands.Get());

    // Add a Window -> 2D Toolkit submenu
    UToolMenu* WindowMenu = ToolMenus->ExtendMenu("LevelEditor.MainMenu.Window");
    FToolMenuSection& WindowSection = WindowMenu->AddSection("WindowUnreal2DToolkit", LOCTEXT("WindowUnreal2DToolkitSection","2D Toolkit"));
    WindowSection.AddMenuEntry("Window.Unreal2DToolkit.OpenToolkit", Commands.OpenToolkit->GetLabel(), Commands.OpenToolkit->GetDescription(), FSlateIcon());

    // Add toolbar buttons to the Level Editor toolbar
    UToolMenu* ToolbarMenu = ToolMenus->ExtendMenu("LevelEditor.LevelEditorToolBar");
    FToolMenuSection& ToolbarSection = ToolbarMenu->AddSection("Unreal2DToolkitToolbar", LOCTEXT("Unreal2DToolkitToolbar","2D Toolkit"));
    ToolbarSection.AddMenuEntry("Unreal2DToolkit.ToolBar.OpenToolkit", Commands.OpenToolkit->GetLabel(), Commands.OpenToolkit->GetDescription(), FSlateIcon());
    ToolbarSection.AddMenuEntry("Unreal2DToolkit.ToolBar.SpriteSlicer", Commands.SpriteSlicer->GetLabel(), Commands.SpriteSlicer->GetDescription(), FSlateIcon());
    ToolbarSection.AddMenuEntry("Unreal2DToolkit.ToolBar.OpenAllTabs", Commands.OpenAllTabs->GetLabel(), Commands.OpenAllTabs->GetDescription(), FSlateIcon());
    ToolbarSection.AddMenuEntry("Unreal2DToolkit.ToolBar.RestoreLayout", Commands.RestoreLayout->GetLabel(), Commands.RestoreLayout->GetDescription(), FSlateIcon());
}

void FUnreal2DToolkitModule::UnregisterMenus()
{
    if (!UToolMenus::IsMenuServiceAvailable())
    {
        return;
    }

    UToolMenus* ToolMenus = UToolMenus::Get();
    ToolMenus->UnregisterOwner(this);
}

// Tab spawn functions
TSharedRef<SDockTab> FUnreal2DToolkitModule::SpawnToolkitTab(const FSpawnTabArgs& Args)
{
    return SNew(SDockTab)
        .TabRole(ETabRole::NomadTab)
        [
            SNew(SUnreal2DToolkitDashboard)
        ];
}

TSharedRef<SDockTab> FUnreal2DToolkitModule::SpawnSpriteSlicerTab(const FSpawnTabArgs& Args)
{
    return SNew(SDockTab)
        .TabRole(ETabRole::NomadTab)
        [
            SNew(SSpriteSlicerPanel)
        ];
}

TSharedRef<SDockTab> FUnreal2DToolkitModule::SpawnFlipbookGeneratorTab(const FSpawnTabArgs& Args)
{
    return SNew(SDockTab)
        .TabRole(ETabRole::NomadTab)
        [
            SNew(SFlipbookGeneratorPanel)
        ];
}

TSharedRef<SDockTab> FUnreal2DToolkitModule::SpawnTilePaletteTab(const FSpawnTabArgs& Args)
{
    return SNew(SDockTab)
        .TabRole(ETabRole::NomadTab)
        [
            SNew(STilePalettePanel)
        ];
}

TSharedRef<SDockTab> FUnreal2DToolkitModule::SpawnAtlasPackerTab(const FSpawnTabArgs& Args)
{
    return SNew(SDockTab)
        .TabRole(ETabRole::NomadTab)
        [
            SNew(SAtlasPackerPanel)
        ];
}

// Helper to execute Python code in-editor via the Python plugin console command
static void ExecutePythonInEditor(const FString& PythonCode)
{
#if WITH_EDITOR
    if (GEditor)
    {
        // Prefix with python console command 'py' which is available when Editor Scripting (Python) plugin is enabled
        FString Cmd = FString::Printf(TEXT("py %s"), *PythonCode.Replace(TEXT("\n"), TEXT("; ")));
        GEditor->Exec(nullptr, *Cmd);
    }
    else
    {
        UE_LOG(LogTemp, Warning, TEXT("GEditor is null; cannot execute Python command: %s"), *PythonCode);
    }
#else
    UE_LOG(LogTemp, Warning, TEXT("Attempted to execute Python code outside editor: %s"), *PythonCode);
#endif
}

// Helper to open a file in the platform default editor
static void OpenFileInDefaultEditor(const FString& RelativePath)
{
    FString BaseDir = FPaths::ConvertRelativePathToFull(FPaths::ProjectDir());
    FString FullPath = FPaths::Combine(BaseDir, RelativePath);
    if (FPaths::FileExists(FullPath))
    {
        FPlatformProcess::LaunchFileInDefaultExternalApplication(*FullPath, nullptr, ELaunchVerb::Open);
    }
    else
    {
        UE_LOG(LogTemp, Warning, TEXT("File not found: %s"), *FullPath);
    }
}

// Replace the OpenSimpleWindow usage in command callbacks with spawning/focusing tabs
void FUnreal2DToolkitModule::OpenToolkit()
{
    FGlobalTabmanager::Get()->TryInvokeTab(ToolkitTabName);
}

void FUnreal2DToolkitModule::OpenSpriteSlicer()
{
    FGlobalTabmanager::Get()->TryInvokeTab(SpriteSlicerTabName);
}

void FUnreal2DToolkitModule::OpenFlipbookGenerator()
{
    // Try to run the included Python helper to create flipbooks from selected sprites
    // The python command will import the module and call the function with a default frame_time
    FString PyCmd = TEXT("import batch_create_flipbooks; batch_create_flipbooks.create_flipbooks_from_selected_sprites(0.08)");
    ExecutePythonInEditor(PyCmd);

    // Also open the Flipbook generator tab
    FGlobalTabmanager::Get()->TryInvokeTab(FlipbookGeneratorTabName);
}

void FUnreal2DToolkitModule::OpenTilePalette()
{
    FGlobalTabmanager::Get()->TryInvokeTab(TilePaletteTabName);
}

void FUnreal2DToolkitModule::OpenAtlasPacker()
{
    FGlobalTabmanager::Get()->TryInvokeTab(AtlasPackerTabName);
}

// Add a new method to open the EditorUtilitySpec YAML in external editor
void FUnreal2DToolkitModule::OpenFlipbookSpec()
{
    // The spec resides under the plugin directory EditorUtilitySpecs/batch_flipbook_spec.yaml
    OpenFileInDefaultEditor(TEXT("Plugins/unreal_2d_toolkit_plugin/EditorUtilitySpecs/batch_flipbook_spec.yaml"));
}

void FUnreal2DToolkitModule::OpenAllTabs()
{
    FGlobalTabmanager::Get()->TryInvokeTab(ToolkitTabName);
    FGlobalTabmanager::Get()->TryInvokeTab(SpriteSlicerTabName);
    FGlobalTabmanager::Get()->TryInvokeTab(FlipbookGeneratorTabName);
    FGlobalTabmanager::Get()->TryInvokeTab(TilePaletteTabName);
    FGlobalTabmanager::Get()->TryInvokeTab(AtlasPackerTabName);
}

void FUnreal2DToolkitModule::RestoreLayout()
{
    // Basic restore: open all tabs and log a hint for the user; precise layout manipulation requires complex FTabManager layout definition
    OpenAllTabs();
    UE_LOG(LogTemp, Log, TEXT("2D Toolkit: Opened all tabs. You can dock them into a layout and use the editor's Window -> Save Layout if desired."));
}

#undef LOCTEXT_NAMESPACE

IMPLEMENT_MODULE(FUnreal2DToolkitModule, Unreal2DToolkit)