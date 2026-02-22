using UnrealBuildTool;
using System.Collections.Generic;

public class Unreal2DToolkit : ModuleRules
{
    public Unreal2DToolkit(ReadOnlyTargetRules Target) : base(Target)
    {
        PCHUsage = PCHUsageMode.UseExplicitOrSharedPCHs;

        PublicDependencyModuleNames.AddRange(new string[] { "Core", "CoreUObject", "Engine", "InputCore" });

        PrivateDependencyModuleNames.AddRange(new string[] { "Slate", "SlateCore", "EditorStyle", "LevelEditor", "Projects", "UnrealEd" });

        // Uncomment if you are using online features
        // PrivateDependencyModuleNames.Add("OnlineSubsystem");

        DynamicallyLoadedModuleNames.AddRange(new string[] { });
    }
}
