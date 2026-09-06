<h1 align="center">Rate Limiter - Bucket4j</h1>

<p align="center">
  <img src="https://img.shields.io/badge/Java-orange?style=flat&logo=openjdk" alt="Java" />
  <img src="https://img.shields.io/badge/Rate_Limit-Bucket4j-purple?logo=apache" alt="Rate Limiting" />
  <br>
    <img src="https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white" alt="Redis" />
  <img src="https://img.shields.io/badge/Spring%20Boot-4.0.2-ED8B00?style=for-the-badge&labelColor=6db33f&color=808080&logo=Spring%20Boot&logoColor=white" alt="Spring Boot Version" />
</p>

-------
<h1>Como utilizar?</h1>
<ul>
  <li>Clone o repositório</li>
  <li>Vá até a pasta do mesmo</li>
  <li>rode o projeto</li>
</ul>
<p>Exemplo detalhado: </p>
<pre>
  git clone https://github.com/PaulooDev12/Rate-Limiter-Requests
  cd caminho_da_pasta_em_que_você clonou // ignore se você clonou diretamente em uma ide
./mvnw spring-boot:run
</pre>

-------
<h1>Cors</h1>
<p>Ao testar em front-end enfrentamos o problema de bloqueio do cors <br> 
como na versão atual não há uma configuração para cors temos dois caminhos à seguir

<h3>Caminho 1: Liberar todas as origens (Mais fácil)></h3>
<pre>
  @CrossOrigin(origins = "*")
</pre>

<h3>Caminho 2: Configurar o WebMvcConfigurer (Recomendando)</h3>
<p>Se você optar por criar mais de um RestController para mais consultas na aplicação, siga os seguintes passos</p>
<ul>
<li>Crie um arquivo de configuração com o nome de sua preferência</li>
<li>Adicione o seguinte conteúdo</li>
</ul>
<pre>
  @Configuration // indica que é uma configuração
  public class Exemplo{
    @Bean 
    public WebMvcConfigurer corsConfig(){
      return new WebMvcConfigurer(){
      @Override
      public void addCorsMappings(CorsRegistry registry){
       registry.addMapping("/**")
        .allowedOrigins("OrigemDoSeuFrontEnd")
        .allowedMethods("*") // libera todos os metódos
      }  
    }
  }
</pre>

-------
<h1>Dominios Front-end</h1>
<p>Ache o domínio da sua tecnologia front-end e adicione no metodo allowedOrigins</p>
<ul>
  <li>Angular: http://localhost:4200</li>
  <li>React: http://localhost:5173</li>
  <li>Vue: http://localhost:5173</li>
  <li>NextJs: http://localhost:3000</li>
</ul>
</p>
