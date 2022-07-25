import com.kld.mes.erp.entity.certWorkHour.*;
import com.kld.mes.erp.service.CertWorkHourServiceImpl;
import com.kld.mes.erp.utils.WsTemplateFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ws.client.core.WebServiceTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: GaoLiang
 * @Date: 2022/7/22 11:10
 */
@Slf4j
public class CertWorkHourServiceTest {

    public static void main(String args[]) {

        CertWorkHourServiceImpl test = new CertWorkHourServiceImpl();

        ObjectFactory objectFactory = new ObjectFactory();

        Zc80Ppif024 zc80Ppif024 = objectFactory.createZc80Ppif024();
        Zc80Ppif024T1 zc80Ppif024T1 = objectFactory.createZc80Ppif024T1();

        Zc80Ppif024S1 zc80Ppif024S1 = new Zc80Ppif024S1();
//        zc80Ppif024S1.setWerks("X092");
//        zc80Ppif024S1.setAufnr("order");
//        zc80Ppif024S1.setVornr("0010");
//        zc80Ppif024S1.setLmnga(new BigDecimal(1));
//        zc80Ppif024S1.setMeinh("件");
//        zc80Ppif024S1.setIle01("MIN");
//
//        zc80Ppif024S1.setIsm01(new BigDecimal(60));
//        zc80Ppif024S1.setFinConf("P");
//        zc80Ppif024S1.setBudat("2022-07-22");
//        zc80Ppif024S1.setZflag("Y");


        List<Zc80Ppif024S1> item = new ArrayList<>();
        item.add(zc80Ppif024S1);

        zc80Ppif024T1.setItem(item);
        zc80Ppif024.setIInput(zc80Ppif024T1);


//        try {
//            marshaller.createMarshaller().marshal(zc80Ppif024, System.out);
//        } catch (JAXBException e) {
//            e.printStackTrace();
//        }
        WsTemplateFactory factory = new WsTemplateFactory();
        WebServiceTemplate webServiceTemplate = factory.generateTemplate("com.kld.mes.erp.entity.certWorkHour");

        Zc80Ppif024Response zc80Ppif024Response = (Zc80Ppif024Response) webServiceTemplate
                .marshalSendAndReceive("http://emaip.erp.cnpc:80/ZBZZ/MES/ZC80_PPIF024/service/PS/PS_ZC80_PPIF024", zc80Ppif024);

        log.debug("recive resp:[{}]", zc80Ppif024Response.getEMes());

    }

}
