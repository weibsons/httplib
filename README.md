Biblioteca de requisiçã HTTP/HTTPS para Android
===============================================

Descrição
---------
Biblioteca para requisição HTTP/HTTPS para Android com suporte a GZIP.
Resposta mais rápida através da compactação dos dados quando o servidor possui suporte a GZIP.
A biblioteca funciona de forma assíncrona, não necessitando criar um job.

Configurando seu Projeto
------------------------

Gradle:
```gradle

dependencies {
    implementation 'mobi.stos:httplib:8'
}

```

Usando a biblioteca
-----------------------

##### Métodos

```java
void get(FutureCallback callback);

void post(FutureCallback callback);

void put(FutureCallback callback);

void delete(FutureCallback callback);

void execute(Method method);
```


As informações abaixo será para explicação com a implementando com a versão do FutureCallback
-------

### Retorno do Callback `FutureCallback`
Em caso de implementação do `FutureCallback`

```java
void onBeforeExecute();

void onAfterExecute();

void onSuccess(int responseCode, @Nullable Object object);

void onFailure(@NonNull Exception exception);
```

Como funciona o retorno do `FutureCallback`
-----------------------

##### void onBeforeExecute();
Executa antes de inicializar o HTTP Request.

##### void onAfterExecute();
Executa SEMPRE após concluir o HTTP Request, mesmo em caso de onSuccess ou onFailure.

##### void onSuccess(int responseCode, @Nullable Object object);
Retorna em caso de conseguir receber o retorno do HTTP, independente do HTTP Status ser do grupo 2xx, 3xx, 4xx ou 5xx.

##### void onFailure(@NonNull Exception exception);
Retorna em caso de erro na requisição ou na resposta do HTTP. Ex> A função está aguardando receber um JSON e recebeu um HTML, ou em caso de erro de não conectividade.


Exemplo do uso com `FutureCallback`:
-----------------------

```java
String url = "http://";
HttpAsync http = new HttpAsync(new URL(url));
http.setAceitarCertificadoInvalido(true); // aceitar SSL inválido (padrão = false)
http.setExecucaoSerial(false); // executa as tarefas do http em forma serial ou assíncrona (padrão = true)
http.addParam("id", 10);
http.addParam("name", "Weibson S'tos");
http.addParam("user", "login", "weibson@stos.mobi");
http.addParam("user", "pass", "202cb962ac59075b964b07152d234b70");
http.post(new FutureCallback() {
    @Override
    public void onBeforeExecute() {
        // to-do onBeforeExecute
    }

    @Override
    public void onAfterExecute() {
        // to-do onAfterExecute -> Executa sempre mesmo que haja sucesso ou erro 
    }

    @Override
    public void onSuccess(int responseCode, @Nullable Object object) {
        // to-do onSuccess -> Executa em caso de não ocorrer erro
    }

    @Override
    public void onFailure(@NonNull Exception exception) {
        // to-do onFailure -> Executa em caso de erro (exception ao receber ou decodificar algo do HTTP). Erro de HTTP status serão interpretados e enviados no onSuccess com seu responseCode
    }
});
```

##### Como ficaria a requisção JSON

```json
{
    "id" : 10,
    "name" : "Weibson S'tos",
    "user" : {
        "login" : "weibson@stos.mobi",
        "pass" : "202cb962ac59075b964b07152d234b70" 
    }
}
```

As informações abaixo será para explicação com a implementando com a versão do SimpleCallback
-------

### Retorno do Callback `SimpleCallback`
Em caso de implementação do `SimpleCallback`
```java
void onCallback(Object ... objects);
```

Como funciona o retorno do `SimpleCallback`
-----------------------

##### void onCallback(Object ... objects);
Executa na chamada do das funções: `addOnSuccessCallback` e `addOnFailureCallback`.<br />
Em caso de acesso através do `addOnSuccessCallback` os parâmetros da função serão:

[0] - Integer - Status Code<br />
[1] - Object

Exemplo do uso com `SimpleCallback`:
-----------------------

```java
String url = "http://";
HttpAsync http = new HttpAsync(new URL(url));
http.setAceitarCertificadoInvalido(true); // aceitar SSL inválido (padrão = false)
http.setExecucaoSerial(false); // executa as tarefas do http em forma serial ou assíncrona (padrão = true)
http.addParam("id", 10);
http.addParam("name", "Weibson S'tos");
http.addParam("user", "login", "weibson@stos.mobi");
http.addParam("user", "pass", "202cb962ac59075b964b07152d234b70");
http.addOnPreExecuteCallback( () -> {});
http.addOnSuccessCallback( objects -> {
    
    int statusCode = (Integer) objects[0];
    JSONArray jsonArray = (JSONArray) objects[1]; // nesse caso pode ser String, JSONObject, JSONArray, HTML, etc.
    
});
http.addOnFailureCallback( objects -> {

    Exception xyz = (Exception) objects[0];

});
http.execute(Method.POST);
```
##### Como ficaria a requisção JSON

```json
{
    "id" : 10,
    "name" : "Weibson S'tos",
    "user" : {
        "login" : "weibson@stos.mobi",
        "pass" : "202cb962ac59075b964b07152d234b70" 
    }
}
```

Criando requisição de UPLOAD com multipart/form-data
-----------------------

Para uso do http/https upload é necessário utilizar a classe `HttpUploadAsync` esssa classe possui parâmetros necessários para o upload de arquivos.

Com a classe `HttpUploadAsync` você poderá enviar arquivos em formato de bytes via `Content-Disposition` como também outros dados extras de um formulário (inputs).

Para o uso do upload é necessário realizar o preenchimento da função `addUploadData` com os seguintes parâmetros:

##### file
Preenchimento com o objeto File

##### fileURI
Preenchimento com o caminho absoluto do file ou nome do arquivo

##### fileContentType
Preenchimento com o Content-Type do arquivo, exemplo: image/jpeg, audio/mp4, application/pdf, etc.

##### input name
Nome de referência que será capturado para reconhecer o arquivo que está sEndo passado via upload no ENDPOINT.

Caso opte enviar mais dados de um formulário, como por exemplo, nome, idade, sexo, será necessário adicionar parâmetros ao `HttpUploadAsync`, então utiliza-se a função `addParam` onde o primeiro parâmetro é a chave e o segundo o valor. 

Exemplo:
nome = "Weibson S'tos" => `addParam("nome", "Weibson S'tos")`
sexo = "M" => `addParam("sexo", "M")`
idade = 30 => `addParam("idade", 30)`


Exemplo do uso do `upload`:
-----------------------

```java
File file = new File( <seu arquivo> );

HttpUploadAsync httpUploadAsync = new HttpUploadAsync(new URL( <sua url> ));
httpUploadAsync.addParam("foo", "bar");
httpUploadAsync.addUploadData(file, file.getAbsolutePath(), "image/png", "arquivo");
httpUploadAsync.addOnSuccessCallback(objects -> {
    Log.v("LOG", "Response Code -> " + objects[0]);
    Log.v("LOG", "Response Body -> " + objects[1]);
});
httpUploadAsync.upload();
```


Licença
-------

Copyright 2017 S'tos Sociedade LTDA.

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.


Contribuições
-------------

Achou e corrigiu um bug ou tem alguma feature em mente e deseja contribuir?

* Faça um fork.
* Adicione sua feature ou correção de bug.
* Envie um pull request no [GitHub].

**S'tos App**

* Nossa Página: http://stos.mobi/

* Nossos Apps: https://play.google.com/store/apps/dev?id=9117205727352262184
