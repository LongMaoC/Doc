# Doc
分析java代码生成文档说明，暂不支持RESTful



```
Config config = new Config.ConfigBuilder()
                    .suffix(suffix)
                    .prefix(prefix)
                    .controllerPath(controllerPath)
                    .docOutPath(docOutPath)
                    .versionList(versionList)
                    .build();
Doc.run(config);
```

