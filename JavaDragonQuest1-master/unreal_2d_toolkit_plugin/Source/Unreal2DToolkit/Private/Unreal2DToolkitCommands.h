#pragma once

#include "Framework/Commands/Commands.h"
#include "EditorStyleSet.h"

class FUnreal2DToolkitCommands : public TCommands<FUnreal2DToolkitCommands>
{
public:
	FUnreal2DToolkitCommands()
		: TCommands<FUnreal2DToolkitCommands>("Unreal2DToolkit", NSLOCTEXT("Contexts", "Unreal2DToolkit", "Unreal 2D Toolkit"), NAME_None, FEditorStyle::GetStyleSetName())
	{
	}

	virtual void RegisterCommands() override;

public:
	TSharedPtr<FUICommandInfo> OpenToolkit;
	TSharedPtr<FUICommandInfo> SpriteSlicer;
	TSharedPtr<FUICommandInfo> FlipbookGenerator;
	TSharedPtr<FUICommandInfo> TilePalette;
	TSharedPtr<FUICommandInfo> AtlasPacker;
	TSharedPtr<FUICommandInfo> OpenFlipbookSpec;
	TSharedPtr<FUICommandInfo> OpenAllTabs;
	TSharedPtr<FUICommandInfo> RestoreLayout;
};