# SegmentableStepsView

可分段步骤进度控件,用户可根据自身需求，选择设置控件样式(水平，垂直，环状，饼状)，可传入颜色数组分别设置不同进度的前景色，颜色数超过最大步数n时，仅取与总步数n的颜色，颜色数量少于总步数时，不路颜色以最后一个颜色补足；环状时，中间部分可设置填充颜色或者图片，同时也可添加文字，文字可选择自动变化大小。

![img](https://github.com/lyx0206331/SegmentableStepsView/blob/master/9r7fy-d8arn.gif)

## 使用方法:

### 第一步:
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

### 第二步:
当前模块build.gradle文件中加入依赖:

	dependencies {
        implementation 'com.github.lyx0206331:SegmentableStepsView:master-SNAPSHOT'
	}

## 参数说明：
| 参数名称  | 参数类型 | 参数说明  |
|:----------|:----------|:----------|
| max_steps    | integer    | 最大步骤数    |
| step_index    | integer    | 当前步骤数    |
| step_background_color    | color/reference    | 背景色    |
| step_colors_array    | reference    | 前景色数组    |
| step_stroke_width    | dimension/reference    | 进度条宽度    |
| step_style    | enum(line_horizontal/line_vertical/ring/circle)    | 样式(水平，垂直，环状，饼状)    |
| step_outside_radius    | dimension/reference    | 环状时外环半径    |
| step_ring_center_color    | color/reference    | 环状时中间填充色    |
| step_ring_center_image    | reference    | 环状时中间填充图片    |
| step_ring_center_text    | string/reference    | 环状时中间文本    |
| step_ring_center_textSize    | dimension/reference    | 环状时中间文本字体大小    |
| step_ring_center_textColor    | dimension/reference    | 环状时中间文本颜色    |
| step_ring_center_textStyle    | enum(normal/bold/italic/bold_italic)    | 环状时中间文本样式(常规/加粗/斜体/加粗斜体)    |
| step_ring_auto_adjust_textSize    | boolean    | 环状时是否自动调节中间文本大小    |


实际代码使用如下：

	<com.adrian.segmentablestepsview.SegmentableStepsView
        android:id="@+id/segmentableStepsView"
        android:layout_width="200dp"
        android:layout_height="200dp"
        android:layout_marginTop="10dp"
        android:paddingStart="5dp"
        android:paddingTop="10dp"
        android:paddingEnd="20dp"
        android:paddingBottom="15dp"
        android:background="@color/black"
        app:max_steps="7"
        app:step_index="0"
        app:step_style="ring"
        app:step_stroke_width="10dp"
        app:step_outside_radius="90dp"
        app:step_ring_center_color="#00f000"
        app:step_background_color="#8888"
        app:step_colors_array="@array/segmentable_step_colors"
        app:step_ring_center_image="@mipmap/ic_launcher"
        app:step_ring_center_text=""
        app:step_ring_center_textStyle="bold_italic"
        app:step_ring_center_textSize="20sp"
        app:step_ring_auto_adjust_textSize="true"
        app:step_ring_center_textColor="@color/design_default_color_primary"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintLeft_toLeftOf="parent" />


设置监听:  
	segmentableStepsView.stepChangeListener = { step ->
		
	}



