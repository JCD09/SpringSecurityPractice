package SpringPractice.Handlers;

import SpringPractice.NewsFeed.Messages.Sources;
import SpringPractice.NewsFeed.NewsWebClient.NewsWebClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;


@Component
public class Handler {

    @Autowired
    NewsWebClient newsWebClient;

    private String NEWS_SOURCES="https://newsapi.org/v2/sources";
    private String API_KEY="X-Api-Key";
    private String API="43a167ad5e5943c386c72685062b81c8";

    public Mono<ServerResponse> helloWorldHTML(ServerRequest serverRequest){
        String currentDir = System.getProperty("user.dir");
        System.out.println("Current dir using System:" +currentDir);
        return ServerResponse.ok().render("../html/Sources",new HashMap<>());
    }



    public Mono<ServerResponse> newsSources(ServerRequest serverRequest){

        WebClient webClient= WebClient.create(NEWS_SOURCES);

        //System.out.println("NEWS API CA:: OCCURED \n");

        MultiValueMap<String,String> map = new LinkedMultiValueMap<>();

        return newsWebClient.getSources(map).
                flatMap(clientResponse ->
                        ServerResponse.ok().body(clientResponse.bodyToMono(String.class),String.class));
    }




    public Mono<ServerResponse> DisplaySession(ServerRequest serverRequest){
        return ServerResponse.
                ok().body(serverRequest.session(),WebSession.class);
    }


    public Mono<ServerResponse> userDetails(ServerRequest serverRequest){
        return serverRequest.
                principal().
                map(p-> UserDetails.class.cast(Authentication.class.cast(p).getPrincipal())).
                flatMap(userDetails->ServerResponse.ok().
                        body(Mono.just(userDetails),UserDetails.class));
    }

    public Mono<ServerResponse> redirect(ServerRequest serverRequest){
        return serverRequest.principal().
                map(Principal::getName).
                map(userName->URI.create("/user/"+userName)).
                flatMap(redirectURI->ServerResponse.
                        permanentRedirect(redirectURI).build());
    }

    public Mono<ServerResponse> loginPage(ServerRequest serverRequest){
        Map<String,Object> data = new HashMap<>();
        return ServerResponse.
                ok().
                contentType(MediaType.TEXT_HTML).render("login",data);}


    public Mono<ServerResponse> DisplayPrincipal(ServerRequest serverRequest){
        Map<String,Object> data = new HashMap<>();
        Authentication principal = Authentication.class.cast(serverRequest.principal());

        return ServerResponse.
                ok().body(Mono.just(principal),Principal.class);
    }

}
