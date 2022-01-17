package com.bsjx.mes.pdm.config;

import com.bsjx.mes.pdm.wsdl.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

@Slf4j
public class PdmClient extends WebServiceGatewaySupport {

    public String getProcessInfo(String xml) {
        GetProcessInfo request = new GetProcessInfo();
        request.setXml(xml);

        log.info("GetProcessInfo Requesting:{}", xml);

        GetProcessInfoResponse response = (GetProcessInfoResponse) getWebServiceTemplate()
                .marshalSendAndReceive(request);

        if (log.isDebugEnabled()) {
            log.debug("GetProcessInfo return:{}", response.getGetProcessInfoReturn());
        }

        return response.getGetProcessInfoReturn();
    }

    public String getBomInfo(String xml) {

        GetBOMInfo request = new GetBOMInfo();
        request.setXml(xml);

        log.info("GetBomInfo requesting:{}", xml);

        GetBOMInfoResponse response = (GetBOMInfoResponse) getWebServiceTemplate()
                .marshalSendAndReceive(request);

        if (log.isDebugEnabled()) {
            log.debug("GetBomInfo return:{}", response.getGetBOMInfoReturn());
        }

        return response.getGetBOMInfoReturn();
    }

    public String getDocumentURL(String xml) {

        GetDocumentURL request = new GetDocumentURL();
        request.setXml(xml);

        log.info("GetDocumentURL requesting:{}", xml);

        GetDocumentURLResponse response = (GetDocumentURLResponse) getWebServiceTemplate()
                .marshalSendAndReceive(request);

        if (log.isDebugEnabled()) {
            log.debug("GetDocumentURL return:{}", response.getGetDocumentURLReturn());
        }

        return response.getGetDocumentURLReturn();
    }
}
