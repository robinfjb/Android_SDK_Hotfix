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
打sdk： _makeSdkJar-》proguardJar  
打patch：_makeSdkJar-》proguardJar-》renameJar-》upzip-》_patchProguardJar-》_md5

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
混淆
```
task proguardJar(type: proguard.gradle.ProGuardTask) {
    String inJar = _makeSdkJar.archivePath.getAbsolutePath()
    println("正在混淆jar...path= " + inJar)

    injars inJar
    outjars "build/libs/classes.jar"
    configuration "$rootDir/sdk/proguard-rules.pro"
}
```

打补丁包
```
task renameJar(type: Copy) {
    from 'build/libs/'
    include 'proguard.jar'
    destinationDir file('build/libs/')
    rename 'proguard.jar', "classes.zip"
}

task upzip(dependsOn: renameJar, type: Copy) {
    def zipFile = file('build/libs/classes.zip')
    def outputDir = file("build/libs/unzip")
    from zipTree(zipFile)
    into outputDir
}

task _patchProguardJar(dependsOn: upzip, type: Jar) {
//指定生成的jar名
    baseName 'patch'
    from('build/libs/unzip/')
    exclude('robin/sdk/hotfix')
    exclude('robin/sdk/proxy')
    exclude('robin/sdk/sdk_common')
    exclude('robin/sdk/service_dynamic')
    doLast {
        delete('build/libs/unzip')
        delete('build/libs/classes.zip')
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

