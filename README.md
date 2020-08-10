# Android_SDK_Hotfix
Android SDK 热更新
具体分析请见 https://juejin.im/post/6858197350486441997

### 1.补丁配置文件结构：`
```
{
    "checksum": {
        "TYPE": "md5",
        "value": "20d81614ab8ac44b3185af0f5e8a96b2"
    },
    "channel": "abcdefgh",
    "version": "1.0.0",
    "subVersion": 1,
    "package": "http://robinfjb.github.io/classes.jar",
    "className": "robin.sdk.sdk_impl.ServiceImpl2"
}
```
### 2.工程结构
sdk-impl（业务实现，补丁覆盖）
sdk-dynamic（补丁下载与应用）
sdk（sdk的入口）
sdk-proxy（sdk与sdk-impl的接口）
sdk-common（工具类）

### 3.打包
打完整的sdk包
```
task _makeSdkJar(type: Jar) {

   //指定生成的jar名
    baseName 'sdk'
    //从哪里打包class文件
    from('build/intermediates/javac/debug/classes/')
    from('../sdk-proxy/build/intermediates/javac/debug/classes/')
    from('../sdk-common/build/intermediates/javac/debug/classes/')
    from('../sdk-dynamic/build/intermediates/javac/debug/classes/')
    from('../sdk-impl/build/intermediates/javac/debug/classes/')
}
_makeSdkJar.dependsOn(clean, 'compileDebugJavaWithJavac')
```

打patch包
```
task _makeHotJar(type: Jar) {
    //指定生成的jar名
    baseName 'hot'
    //从哪里打包class文件
    from('build/intermediates/javac/release/classes/')
}
_makeHotJar.dependsOn(clean,'compileReleaseJavaWithJavac')
```

打dex包
```
task _jarToDex(type: Exec) {
    //借助windows的cmd命令行执行
    commandLine 'cmd'

    doFirst {
        //jar文件对象
        def srcFile = file("/build/libs/hot.jar")
        //需要生成的dex文件对象
        def desFile = file(srcFile.parent + "/" + "dex.jar")

        //此行可以不需要
        workingDir srcFile.parent

        //拼接dx.bat执行的参数
        def list = []
        list.add("/c")
        list.add("dx")
        list.add("--dex")
        list.add("--output")
        list.add(desFile)
        list.add(srcFile)

        //设置参数到cmd命令行
        args list
    }
}
```

包的MD5
```
task _md5() {
    doLast {
        def file = file("build/libs/dex.jar")
        println(generateMd5(file))
    }
}
```

