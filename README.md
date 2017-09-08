# AndMBaseUtils_6.0
android快速开发框架，HHActivity中封装了动态权限请求逻辑
## To get a Git project into your build:
## Step 1. Add the JitPack repository to your build file 
### Add it in your root build.gradle at the end of repositories:
```
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}      
```
## Step 2. Add the dependency
```  
dependencies {
    compile 'com.github.mtjsoft:AndMBaseUtils_6.0:V1.0.0'
}
```
### 3、Error
```
Error:Execution failed for task ':app:processDebugManifest'.
 > Manifest merger failed with multiple errors, see logs
```
#### Resolvent：
```
在app目录下，Manifest.xml 的 application 标签下添加   tools:replace="android:icon, android:theme"  
（多个属性用,隔开，并且记住在manifest根标签上加入
 xmlns:tools="http://schemas.android.com/tools"   ，否则会找不到namespace哦）
```
