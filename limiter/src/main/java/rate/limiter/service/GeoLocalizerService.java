package rate.limiter.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;


@Service
public class GeoLocalizerService {

    private final WebClient geoWebClient;
    private final WebClient ipDiscoverClient;
    public GeoLocalizerService(WebClient.Builder webClientBuilder) {
        this.geoWebClient = webClientBuilder.clone()
                .baseUrl("http://ip-api.com/json/")
                .build();
        this.ipDiscoverClient = webClientBuilder.clone()
                .baseUrl("https://api.ipify.org")
                .build();
    }

    public LocalizationInfo getLocation(String clientIp) {

        if(isLocalHostOrPrivateIp(clientIp)){
            clientIp = getRealIp();
        }
        try{
            return this.geoWebClient.get()
                    .uri("{ip}",clientIp)
                    .retrieve()
                    .bodyToMono(LocalizationInfo.class)
                    .block();
        }catch (Exception e){
            return new LocalizationInfo("Unknow", "Unknow", "Unknow", "Unknow");
        }
    }
    public boolean isLocalHostOrPrivateIp(String ip) {
        return ip.equals("127.0.0.1")
                || ip.equals("0:0:0:0:0:0:0:1")
                || ip.startsWith("192.168.")
                || ip.startsWith("10.");
    }
    public String getRealIp() {
        try{
            return this.ipDiscoverClient.get()
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (Exception e) {
            return "8:8:8:8";
        }
    }
}
