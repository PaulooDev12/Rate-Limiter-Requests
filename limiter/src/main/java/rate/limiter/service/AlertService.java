package rate.limiter.service;


import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AlertService {
    private final GeoLocalizerService geoLocalizerService;
    public AlertService(GeoLocalizerService geoLocalizerService) {
        this.geoLocalizerService = geoLocalizerService;
    }
    @Async("asyncManager")
    public void processAndAlert(String ip) {
        LocalizationInfo localInfo = geoLocalizerService.getLocation(ip);
        String[] attributes = {localInfo.country(), localInfo.regionName(), localInfo.city(), localInfo.isp()};
        String[] info = {"País: ", "Região: ", "Cidade: ", "Provedor: "};
        for(int i = 0; i < attributes.length; i++){
            System.out.println(info[i] + attributes[i]);
        }
    }
}
