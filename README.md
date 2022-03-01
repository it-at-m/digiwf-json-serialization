<div id="top"></div>

<!-- PROJECT SHIELDS -->
[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![MIT License][license-shield]][license-url]


<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/it-at-m/digiwf-json-serialization">
    <img src="images/logo.png" alt="Logo" height="200">
  </a>

<h3 align="center">DigiWF JSON Serialization</h3>

  <p align="center">
    This is a Spring Boot Starter library to serialize json schemas
     <!-- <br />
   <a href="https://github.com/it-at-m/digiwf-json-serialization"><strong>Explore the docs »</strong></a> -->
    <br />
    <br />
     <!-- <a href="https://github.com/it-at-m/digiwf-json-serialization">View Demo</a>
    · -->
    <a href="https://github.com/it-at-m/digiwf-json-serialization/issues">Report Bug</a>
    ·
    <a href="https://github.com/it-at-m/digiwf-json-serialization/issues">Request Feature</a>
  </p>
</div>



<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
    </li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->
## About The Project

DigiWF JSON Serialization serializes and deserializes data based on json schemas.

**Features**

* **Serialize:** The serialize function creates a json object based on the json schema and the data you provide (current and previous data). The current data is used if the property is not readonly or null. Otherwise, the previous data is added to data in the serialization process.
* **Deserialize:** The deserialize functionality takes a json object and returns the relevant parts of it based on the json schema.

At the moment only JSON schema [Draft v7](https://json-schema.org/draft-07/json-schema-release-notes.html) is supported.

<p align="right">(<a href="#top">back to top</a>)</p>


### Built With

This project is built with:

* [Spring Boot](https://spring.io/projects/spring-boot)
* [everit-json-schema](https://github.com/everit-org/json-schema)

<p align="right">(<a href="#top">back to top</a>)</p>


<!-- GETTING STARTED -->
## Getting Started

_Below is an example of how you can installing and setup up your service_

1. Use the spring initalizer and create a Spring Boot application with `Spring Web`
   dependencies [https://start.spring.io](https://start.spring.io)
2. Add the digiwf-json-serialization dependency

With Maven:

```
   <dependency>
        <groupId>io.muenchendigital.digiwf</groupId>
        <artifactId>digiwf-json-serialization-starter</artifactId>
        <version>${digiwf.version}</version>
   </dependency>
```

With Gradle:

```
implementation group: 'io.muenchendigital.digiwf', name: 'digiwf-json-serialization-starter', version: '${digiwf.version}'
```

3. Inject the `JsonSchemaSerializationService` in your application


<!-- USAGE EXAMPLES -->
## Usage

The library has several functionalities that can be configured. We have provided examples that show how you can use
them.

_For more examples, please refer to the [Examples](example) folder_

First inject the `JsonSchemaSerializationService` in your class. The `JsonSchemaSerializationService` is a 
wrapper around a serializer instance which provides serialize and deserialize methods. 

```java
@RequiredArgsConstructor
public class YourClass {
    // inject JsonSchemaSerializationService
    private final JsonSchemaSerializationService jsonSchemaSerializationService;
}
```

Then you can call `jsonSchemaSerializationService.serialize(schema, data, previousData)` to serialize data based on
the json schema you are providing.
To deserialize data you can call `jsonSchemaSerializationService.deserialize(schema, data)`.

### Create a custom serializer

If you want to use a custom serializer create a serializer which implements the `JsonSchemaBaseSerializer` 
and create a bean that provides your custom serializer in the configuration.
Then your custom serializer is used in the `JsonSchemaSerializationService` instead of the default one.

```java
public class MyCustomJsonSchemaSerializer implements JsonSchemaBaseSerializer {
   @Override
   public Map<String, Object> serialize(Schema schema, JSONObject data, JSONObject previousData) {
      return null;
   }

   @Override
   public Map<String, Object> deserialize(Schema schema, Map<String, Object> data) {
      return null;
   }
}
```

```java
@Configuration
public class MyJsonSerializationAutoConfiguration {
   @Bean
   public JsonSchemaBaseSerializer customJsonSchemaSerializer() {
      return new MyCustomJsonSchemaSerializer();
   }    
}
```


<!-- CONTRIBUTING -->
## Contributing

Contributions are what make the open source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

If you have a suggestion that would make this better, please fork the repo and create a pull request. You can also simply open an issue with the tag "enhancement".
Don't forget to give the project a star! Thanks again!

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

<p align="right">(<a href="#top">back to top</a>)</p>


<!-- LICENSE -->
## License

Distributed under the MIT License. See `LICENSE` for more information.

<p align="right">(<a href="#top">back to top</a>)</p>


<!-- CONTACT -->
## Contact

it@m - opensource@muenchendigital.io

<p align="right">(<a href="#top">back to top</a>)</p>


<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[contributors-shield]: https://img.shields.io/github/contributors/it-at-m/digiwf-json-serialization.svg?style=for-the-badge
[contributors-url]: https://github.com/it-at-m/digiwf-json-serialization/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/it-at-m/digiwf-json-serialization.svg?style=for-the-badge
[forks-url]: https://github.com/it-at-m/digiwf-json-serialization/network/members
[stars-shield]: https://img.shields.io/github/stars/it-at-m/digiwf-json-serialization.svg?style=for-the-badge
[stars-url]: https://github.com/it-at-m/digiwf-json-serialization/stargazers
[issues-shield]: https://img.shields.io/github/issues/it-at-m/digiwf-json-serialization.svg?style=for-the-badge
[issues-url]: https://github.com/it-at-m/digiwf-json-serialization/issues
[license-shield]: https://img.shields.io/github/license/it-at-m/digiwf-json-serialization.svg?style=for-the-badge
[license-url]: https://github.com/it-at-m/digiwf-json-serialization/blob/master/LICENSE
[product-screenshot]: images/screenshot.png
