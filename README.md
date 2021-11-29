# SegmentableStepsView
可分段步骤控件

[![](https://jitpack.io/v/lyx0206331/SegmentableStepsView.svg)](https://jitpack.io/#lyx0206331/SegmentableStepsView)

使用方法:  
Gradle 7.0.3之前的版本，将以下代码加入根目录下build.gradle存储库末尾:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

Gradle 7.0.3及之后的版本，将以下代码加入settings.gradle文件中:

	dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        ...
        jcenter()
        maven { url 'https://jitpack.io' }
    }
}


Step 2. Add the dependency

	dependencies {
	        implementation 'com.github.lyx0206331:SegmentableStepsView:Tag'
	}
 
