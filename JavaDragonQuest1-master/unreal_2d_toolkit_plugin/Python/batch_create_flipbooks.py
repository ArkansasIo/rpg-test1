# Example Unreal Editor Python script
# Requires Editor Scripting (Python) plugin enabled in UE5

import unreal

@unreal.uclass()
class FlipbookBuilder(unreal.GlobalEditorUtilityBase):
    pass

def create_flipbooks_from_selected_sprites(frame_time=0.1):
    selected_assets = unreal.EditorUtilityLibrary.get_selected_assets()
    sprites = [a for a in selected_assets if isinstance(a, unreal.PaperSprite)]
    if not sprites:
        unreal.log_warning('No sprites selected')
        return

    factory = unreal.PaperFlipbookFactory()
    for s in sprites:
        name = s.get_name() + '_flipbook'
        asset_tools = unreal.AssetToolsHelpers.get_asset_tools()
        pkg_path = '/Game/Flipbooks'
        flipbook = asset_tools.create_asset(name, pkg_path, unreal.PaperFlipbook, factory)
        flipbook.add_keyframe(s, frame_time)
        unreal.EditorAssetLibrary.save_asset(pkg_path + '/' + name)
        unreal.log('Created flipbook: {}'.format(name))

# To run in the editor:
# import batch_create_flipbooks
# batch_create_flipbooks.create_flipbooks_from_selected_sprites(0.08)
