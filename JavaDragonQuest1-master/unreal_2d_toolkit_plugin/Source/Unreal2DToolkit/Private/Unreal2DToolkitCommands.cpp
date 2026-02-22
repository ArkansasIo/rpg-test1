#include "Unreal2DToolkitCommands.h"
#include "Framework/Commands/UICommandInfo.h"

#define LOCTEXT_NAMESPACE "FUnreal2DToolkitCommands"

void FUnreal2DToolkitCommands::RegisterCommands()
{
    UI_COMMAND(OpenToolkit, "Open 2D Toolkit", "Open the 2D Toolkit dashboard", EUserInterfaceActionType::Button, FInputChord());
    UI_COMMAND(SpriteSlicer, "Sprite Slicer", "Open the sprite slicer utility", EUserInterfaceActionType::Button, FInputChord());
    UI_COMMAND(FlipbookGenerator, "Flipbook Generator", "Create flipbooks from selected sprites", EUserInterfaceActionType::Button, FInputChord());
    UI_COMMAND(TilePalette, "Tile Palette", "Open Tile Palette editor", EUserInterfaceActionType::Button, FInputChord());
    UI_COMMAND(AtlasPacker, "Atlas Packer", "Pack selected sprites into an atlas", EUserInterfaceActionType::Button, FInputChord());
    UI_COMMAND(OpenFlipbookSpec, "Open Flipbook Spec", "Open the EditorUtility YAML spec for flipbook batching", EUserInterfaceActionType::Button, FInputChord());
    UI_COMMAND(OpenAllTabs, "Open All 2D Tabs", "Open all 2D toolkit tabs", EUserInterfaceActionType::Button, FInputChord(EKeys::T, EModifierKey::Control | EModifierKey::Alt));
    UI_COMMAND(RestoreLayout, "Restore 2D Layout", "Restore the 2D toolkit tab layout", EUserInterfaceActionType::Button, FInputChord(EKeys::L, EModifierKey::Control | EModifierKey::Alt));
}

#undef LOCTEXT_NAMESPACE