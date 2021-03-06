## bigfix-relevance-spark ##
This is a simple unofficial spark wrapper for the relevance query functionality of the Bigfix &reg; REST API

### Usage ###
```scala
val customSchema = 
      StructType(
        StructField("computerName", StringType, true) ::
        StructField("computerId", IntegerType, true) 
          :: Nil)

//RESTAPI
val besDf = sqlContext.read.format("com.github.eyce9000.spark.bes")
        .schema(customSchema)
        .options(Map[String,String](
          "url" -> "https://my.besserver.com:52311/",
          "username" -> "username",
          "password" -> "password",
          "relevanceQuery" -> "(name of it, id of it) of bes computers",
          "readTimeout" -> "60000",
          "connTimeout" -> "5000")).load()

//Webreports
val besDf = sqlContext.read.format("com.github.eyce9000.spark.bes")
        .schema(customSchema)
        .options(Map[String,String](
          "url" -> "https://my.besserver.com:443/",
          "username" -> "webreportsusername",
          "password" -> "webreportspassword",
          "relevanceQuery" -> "(name of it, id of it) of bes computers",
          "readTimeout" -> "60000",
          "connTimeout" -> "5000",
          "clientType" -> "webreports")).load()

//Ignore HTTPS Certificate issues
val besDf = sqlContext.read.format("com.github.eyce9000.spark.bes")
        .schema(customSchema)
        .options(Map[String,String](
          "url" -> "https://my.besserver.com:443/",
          "username" -> "webreportsusername",
          "password" -> "webreportspassword",
          "relevanceQuery" -> "(name of it, id of it) of bes computers",
          "readTimeout" -> "60000",
          "connTimeout" -> "5000",
          "clientType" -> "webreports",
          "insecure" -> "true")).load()
```
Note that you must provide a schema as bigfix queries do not provide a functionality to name the result columns.