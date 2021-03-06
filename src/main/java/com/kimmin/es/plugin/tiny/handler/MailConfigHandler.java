package com.kimmin.es.plugin.tiny.handler;

import com.kimmin.es.plugin.tiny.service.MailService;
import org.codehaus.jackson.map.ObjectMapper;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.rest.*;

import java.io.IOException;
import java.util.Map;

/**
 * Created by kimmin on 3/8/16.
 */
public class MailConfigHandler implements RestHandler {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Inject
    public MailConfigHandler(RestController controller){
        controller.registerHandler(RestRequest.Method.POST,"/tiny/mail",this);
    }

    public void handleRequest(final RestRequest restRequest, final RestChannel channel){
        try{
            Map map = objectMapper.readValue(restRequest.content().toBytes(),Map.class);
            if(restRequest.hasParam("host.smtp")
                    && restRequest.hasParam("host.name")
                    && restRequest.hasParam("host.password")) {
                String smtpHost = (String) map.get("host.smtp");
                String username = (String) map.get("host.name");
                String password = (String) map.get("host.password");
                MailService.getInstance().setConfiguration(username, password, smtpHost);
                channel.sendResponse(new BytesRestResponse(RestStatus.OK, "CONFIG SUCCESS!"));
            }else{
                channel.sendResponse(new BytesRestResponse(RestStatus.OK, "CONFIG FAILURE! - Check param!"));
            }
        }catch (IOException ioe){
            ioe.fillInStackTrace();
        }

    }

}
