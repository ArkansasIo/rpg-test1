#pragma once

#include "Unreal2DToolkitHelpers.h"
#include "Unreal2DToolkit.h"

#include "Widgets/SCompoundWidget.h"
#include "Widgets/DeclarativeSyntaxSupport.h"
#include "Widgets/Input/SNumericEntryBox.h"
#include "Widgets/Input/SButton.h"
#include "Widgets/Text/STextBlock.h"
#include "Widgets/Layout/SVerticalBox.h"
#include "Widgets/Layout/SHorizontalBox.h"
#include "Framework/Docking/TabManager.h"

class SUnreal2DToolkitDashboard : public SCompoundWidget
{
public:
    SLATE_BEGIN_ARGS(SUnreal2DToolkitDashboard) {}
    SLATE_END_ARGS()

    void Construct(const FArguments& InArgs)
    {
        ChildSlot
        [
            SNew(SVerticalBox)
            + SVerticalBox::Slot().AutoHeight().Padding(4)
            [
                SNew(STextBlock).Text(NSLOCTEXT("Unreal2DToolkit", "Dashboard", "2D Toolkit Dashboard"))
            ]
            + SVerticalBox::Slot().AutoHeight().Padding(4)
            [
                SNew(SHorizontalBox)
                + SHorizontalBox::Slot().AutoWidth().Padding(2)
                [
                    SNew(SButton)
                    .Text(NSLOCTEXT("Unreal2DToolkit", "OpenSpriteSlicer", "Sprite Slicer"))
                    .OnClicked_Lambda([]() { FGlobalTabmanager::Get()->TryInvokeTab(FUnreal2DToolkitModule::SpriteSlicerTabName); return FReply::Handled(); })
                ]
                + SHorizontalBox::Slot().AutoWidth().Padding(2)
                [
                    SNew(SButton)
                    .Text(NSLOCTEXT("Unreal2DToolkit", "OpenFlipbookGenerator", "Flipbook Generator"))
                    .OnClicked_Lambda([]() { FGlobalTabmanager::Get()->TryInvokeTab(FUnreal2DToolkitModule::FlipbookGeneratorTabName); return FReply::Handled(); })
                ]
                + SHorizontalBox::Slot().AutoWidth().Padding(2)
                [
                    SNew(SButton)
                    .Text(NSLOCTEXT("Unreal2DToolkit", "OpenTilePalette", "Tile Palette"))
                    .OnClicked_Lambda([]() { FGlobalTabmanager::Get()->TryInvokeTab(FUnreal2DToolkitModule::TilePaletteTabName); return FReply::Handled(); })
                ]
                + SHorizontalBox::Slot().AutoWidth().Padding(2)
                [
                    SNew(SButton)
                    .Text(NSLOCTEXT("Unreal2DToolkit", "OpenAtlasPacker", "Atlas Packer"))
                    .OnClicked_Lambda([]() { FGlobalTabmanager::Get()->TryInvokeTab(FUnreal2DToolkitModule::AtlasPackerTabName); return FReply::Handled(); })
                ]
            ]
        ];
    }
};

class SSpriteSlicerPanel : public SCompoundWidget
{
public:
    SLATE_BEGIN_ARGS(SSpriteSlicerPanel) {}
    SLATE_END_ARGS()

    void Construct(const FArguments& InArgs)
    {
        ChildSlot
        [
            SNew(SVerticalBox)
            + SVerticalBox::Slot().AutoHeight().Padding(4)
            [
                SNew(STextBlock).Text(NSLOCTEXT("Unreal2DToolkit", "SpriteSlicer", "Sprite Slicer"))
            ]
            + SVerticalBox::Slot().AutoHeight().Padding(4)
            [
                SNew(SButton)
                .Text(NSLOCTEXT("Unreal2DToolkit", "RunSpriteSlicer", "Slice Selected Texture(s)"))
                .OnClicked(this, &SSpriteSlicerPanel::OnRunSlicerClicked)
            ]
        ];
    }

private:
    FReply OnRunSlicerClicked()
    {
        // Placeholder: run a Python stub or log a message
        FString PyCmd = TEXT("import unreal; unreal.log('Sprite slicer not implemented (stub)')");
        ExecutePythonInEditor(PyCmd);
        return FReply::Handled();
    }
};

class SFlipbookGeneratorPanel : public SCompoundWidget
{
public:
    SLATE_BEGIN_ARGS(SFlipbookGeneratorPanel) {}
    SLATE_END_ARGS()

    void Construct(const FArguments& InArgs)
    {
        FrameTime = 0.08f;

        ChildSlot
        [
            SNew(SVerticalBox)
            + SVerticalBox::Slot().AutoHeight().Padding(4)
            [
                SNew(STextBlock).Text(NSLOCTEXT("Unreal2DToolkit", "FlipbookGenerator", "Flipbook Generator"))
            ]
            + SVerticalBox::Slot().AutoHeight().Padding(4)
            [
                SNew(SHorizontalBox)
                + SHorizontalBox::Slot().AutoWidth().VAlign(VAlign_Center).Padding(2)
                [
                    SNew(STextBlock).Text(NSLOCTEXT("Unreal2DToolkit", "FrameTimeLabel", "Frame Time:"))
                ]
                + SHorizontalBox::Slot().AutoWidth().Padding(2)
                [
                    SNew(SNumericEntryBox<float>)
                    .Value(this, &SFlipbookGeneratorPanel::GetFrameTime)
                    .OnValueChanged(this, &SFlipbookGeneratorPanel::OnFrameTimeChanged)
                    .MinValue(0.01f)
                    .MaxValue(1.0f)
                ]
            ]
            + SVerticalBox::Slot().AutoHeight().Padding(4)
            [
                SNew(SButton)
                .Text(NSLOCTEXT("Unreal2DToolkit", "CreateFlipbooksButton", "Create Flipbooks from Selected Sprites"))
                .OnClicked(this, &SFlipbookGeneratorPanel::OnCreateFlipbooksClicked)
            ]
        ];
    }

private:
    TOptional<float> GetFrameTime() const { return FrameTime; }
    void OnFrameTimeChanged(float NewValue) { FrameTime = NewValue; }

    FReply OnCreateFlipbooksClicked()
    {
        FString PyCmd = FString::Printf(TEXT("import batch_create_flipbooks; batch_create_flipbooks.create_flipbooks_from_selected_sprites(%f)"), FrameTime);
        ExecutePythonInEditor(PyCmd);
        return FReply::Handled();
    }

private:
    float FrameTime;
};

class STilePalettePanel : public SCompoundWidget
{
public:
    SLATE_BEGIN_ARGS(STilePalettePanel) {}
    SLATE_END_ARGS()

    void Construct(const FArguments& InArgs)
    {
        ChildSlot
        [
            SNew(SVerticalBox)
            + SVerticalBox::Slot().AutoHeight().Padding(4)
            [
                SNew(STextBlock).Text(NSLOCTEXT("Unreal2DToolkit", "TilePalette", "Tile Palette"))
            ]
            + SVerticalBox::Slot().AutoHeight().Padding(4)
            [
                SNew(SButton)
                .Text(NSLOCTEXT("Unreal2DToolkit", "OpenTilePaletteButton", "Open Tile Palette Editor (stub)"))
                .OnClicked_Lambda([]() { FString PyCmd = TEXT("import unreal; unreal.log('Tile Palette opened (stub)')"); ExecutePythonInEditor(PyCmd); return FReply::Handled(); })
            ]
        ];
    }
};

class SAtlasPackerPanel : public SCompoundWidget
{
public:
    SLATE_BEGIN_ARGS(SAtlasPackerPanel) {}
    SLATE_END_ARGS()

    void Construct(const FArguments& InArgs)
    {
        ChildSlot
        [
            SNew(SVerticalBox)
            + SVerticalBox::Slot().AutoHeight().Padding(4)
            [
                SNew(STextBlock).Text(NSLOCTEXT("Unreal2DToolkit", "AtlasPacker", "Atlas Packer"))
            ]
            + SVerticalBox::Slot().AutoHeight().Padding(4)
            [
                SNew(SButton)
                .Text(NSLOCTEXT("Unreal2DToolkit", "PackAtlasButton", "Pack Selected Sprites into Atlas (stub)"))
                .OnClicked_Lambda([]() { FString PyCmd = TEXT("import unreal; unreal.log('Atlas packer not implemented (stub)')"); ExecutePythonInEditor(PyCmd); return FReply::Handled(); })
            ]
        ];
    }
};