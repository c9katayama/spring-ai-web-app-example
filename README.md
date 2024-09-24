# Spring AI WebApp Example

Spring AIの各機能をVaadinで実行できるサンプルプロジェクトです。

## Prerequirement

- Java 17
- src/main/resourcesの secure-blank.properties を secure.properties にリネーム
- Azure OpenAI/ OpenAIを使う場合は、secure.propertiesにendpointとAPIkeyを設定
- Amazon Bedrockを使う場合、.aws/credentialsに"spring-ai-web-app-example-credential-profile"のrole名でcredentialを設定
- https://huggingface.co/intfloat/multilingual-e5-base/tree/main/onnx から、model.onnx をダウンロドして、/src/main/resources/transformer/multilingual-e5-baseに保存

## その他

- VectorStoreには、SimpleVectorStoreを設定
- Embeddingは、TransformersEmbeddingModelを設定。モデルはsrc/main/resources/transformerにある multilingual-e5-base を設定