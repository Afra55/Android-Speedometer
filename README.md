
![speedometer_2](https://raw.githubusercontent.com/Afra55/Speedometer/master/speedometer_banner.jpeg)

[![license](https://raw.githubusercontent.com/Afra55/Speedometer/master/license.svg)](https://github.com/Afra55/Speedometer/blob/master/LICENSE)

[TOC]



## Speedometer
速度表盘 For Android

## 参数说明

|  参数   | 说明  |
|  ----  | ----  |
| meterDividerAreaNumber  | 表盘平均划分区域数量 |
| meterBg  | 表盘背景   |
| meterMaskBg  | 表盘遮照图   |
| meterPointer  | 表盘指针图, 指针图要垂直向下　   |
| meterBottomEmptyAngle  | 表盘底部0刻度与最大刻度的夹角， 比如 45度 |
| meterNumberMargin  | 表盘刻度距离边距的距离 |
| meterNumberTextSize  | 表盘刻度数字文字大小 |
| meterNumberTextColor  | 表盘刻度数字颜色 |
| meterNumberSelectedTextColor  | 指针划过区域的表盘刻度数字颜色 |
| meterNumberLimitTextColor  | 指针划过区域的超过限速数字的表盘刻度数字颜色 |
| meterNumberFontAssetPath  | 表盘刻度数字字体，asset文件夹路径，比如：`font/DIN_Condensed_Bold.ttf` |
| meterCenterFontAssetPath  | 表盘中心数字字体 |
| meterCenterNumberTextSize  | 表盘中心数字文字大小 |
| meterCenterDescTextSize  | 表盘中心描述文字大小 |
| meterCenterTextColor  | 表盘中心数字文字颜色   |
| meterCenterDescTextColor  | 表盘中心描述文字颜色   |
| meterCenterDesc  | 表盘中心描述字符串 |
| meterCenterIc  | 表盘中心文字的背景图　   |
| meterType  | 表盘类型，针对特殊表盘，默认0；1：只有指针指向的刻度变颜色，中间数字在描述下面；2： 隐藏指针，隐藏刻度, 背景和蒙板图片都绘制到 mask 上  |
| meterRotateX  | 表盘绕x轴旋转的角度   |
| meterTranslateZ  | 表盘z轴移动的距离   |
| meterTranslateY  | 表盘Y轴移动的距离   |
| meterHideCenterNumber  | 是否隐藏中心数字　   |
| meterHideDividerNumber  | 是否隐藏刻度数字　   |

## 原理图
![原理图](https://raw.githubusercontent.com/Afra55/Speedometer/master/schematic_diagram.png)


## 效果展示

### 表盘 1
```
  <com.afra55.speedometer.SpeedometerDialog
        android:id="@+id/test_speedometer"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:meterBg="@drawable/dialog_bg_1"
        app:meterCenterDesc="km/h"
        app:meterCenterIc="@drawable/dialog_center_icon_1"
        app:meterDividerAreaNumber="8"
        app:meterMaskBg="@drawable/dialog_mask_bg_1"
        app:meterNumberLimitTextColor="#E30808"
        app:meterNumberMargin="89dp"
        app:meterNumberSelectedTextColor="#FFFFFF"
        app:meterCenterNumberTextSize="18sp"
        app:meterCenterDescTextSize="18sp"
        app:meterCenterTextColor="#FFFFFF"
        app:meterCenterDescTextColor="#FFFFFF"
        app:meterNumberTextColor="#4DFFFFFF"
        app:meterNumberTextSize="20sp"
        app:meterPointer="@drawable/dialog_pointer_1"
        />

```

![speedometer_1](https://raw.githubusercontent.com/Afra55/Speedometer/master/gif/speedometer_1.gif)

### 表盘 2

![speedometer_2](https://raw.githubusercontent.com/Afra55/Speedometer/master/gif/speedometer_2.gif)

### 表盘 3

![speedometer_3](https://raw.githubusercontent.com/Afra55/Speedometer/master/gif/speedometer_3.gif)

### 表盘 4

![speedometer_4](https://raw.githubusercontent.com/Afra55/Speedometer/master/gif/speedometer_4.gif)

### 表盘 5
![speedometer_5](https://raw.githubusercontent.com/Afra55/Speedometer/master/gif/speedometer_5.gif)

## 基础知识

### xfermode
用来进行图像混合处理。一共有18种混合模式。[官方文档](https://developer.android.com/reference/android/graphics/PorterDuff.Mode)
```
 Paint paint = new Paint();
 // DST 图
 canvas.drawBitmap(destinationImage, 0, 0, paint);

 PorterDuff.Mode mode = // choose a mode
 paint.setXfermode(new PorterDuffXfermode(mode));

 // SRC 图
 canvas.drawBitmap(sourceImage, 0, 0, paint);
```
![xfermode](https://raw.githubusercontent.com/Afra55/Speedometer/master/doc/picture/xfermode.png)

### 测量文字大小
![font_size](https://raw.githubusercontent.com/Afra55/Speedometer/master/doc/picture/font.png)
获取文字的内容高度，这个高度是文字绘制的最高点到最低点的距离：
```
fun TextPaint.getTextBound(str: String): Rect {
    val rect = Rect()
    getTextBounds(str, 0, str.length, rect)
    return rect
}

fun TextPaint.getCapHeight(): Int {
    // 获得0-9数字内容的高度
    return getTextBound("1234567890").height()
}
```
### dp2px

```
fun Resources.dp2Px(dip: Float): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, this.displayMetrics)
}
```

### 正方形 view
```
class SquareFrameLayout : FrameLayout {

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(attrs, defStyle)
    }

    fun init(attrs: AttributeSet?, defStyle: Int) {

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        var width = MeasureSpec.getSize(widthMeasureSpec)
        
        if (heightMode == MeasureSpec.AT_MOST || heightMode == MeasureSpec.EXACTLY) {
            val height = MeasureSpec.getSize(heightMeasureSpec)
            // 如果高是 match_parent 或者指定了特定值, 则取宽高较小的值为整个view的宽高
            width = Math.min(width, height)
        }
        
        setMeasuredDimension(width, width)
    }
}
```

## license
Speedometer is available under the Apache-2.0 license. See the LICENSE file for more info.
```
   Copyright 2020 Afra55

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

```