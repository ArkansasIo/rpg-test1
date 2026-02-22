#pragma once

#include "CoreMinimal.h"
#include "Modules/ModuleManager.h"

class FUnreal2DToolkitModule : public IModuleInterface
{
public:
    virtual void StartupModule() override;
    virtual void ShutdownModule() override;

    // Registration helpers
    void RegisterMenus();
    void UnregisterMenus();

    // Command callbacks
    void OpenToolkit();
    void OpenSpriteSlicer();
    void OpenFlipbookGenerator();
    void OpenTilePalette();
    void OpenAtlasPacker();
    void OpenFlipbookSpec();
    void OpenAllTabs();
    void RestoreLayout();

    // Tab spawn helpers
    static const FName ToolkitTabName;
    static const FName SpriteSlicerTabName;
    static const FName FlipbookGeneratorTabName;
    static const FName TilePaletteTabName;
    static const FName AtlasPackerTabName;

    TSharedRef<class SDockTab> SpawnToolkitTab(const class FSpawnTabArgs& Args);
    TSharedRef<class SDockTab> SpawnSpriteSlicerTab(const class FSpawnTabArgs& Args);
    TSharedRef<class SDockTab> SpawnFlipbookGeneratorTab(const class FSpawnTabArgs& Args);
    TSharedRef<class SDockTab> SpawnTilePaletteTab(const class FSpawnTabArgs& Args);
    TSharedRef<class SDockTab> SpawnAtlasPackerTab(const class FSpawnTabArgs& Args);

    // Submenu builder for Window menu
    void AddWindowSubMenu(class UToolMenu* InMenu);
};