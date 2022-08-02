import com.kld.mes.erp.entity.storage.*;
import com.kld.mes.erp.service.StorageServiceImpl;
import com.kld.mes.erp.utils.WsTemplateFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ws.client.core.WebServiceTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * 向ERP推送工艺
 *
 * @Author: mafeng02
 * @Date: 2022/8/1 09:00:00
 */
@Slf4j
public class StorageTest {

    public static void main(String args[]) {

        StorageServiceImpl test = new StorageServiceImpl();
        String[] materialNos = {"20000012"};
        String werks = "X092";
        test.getStorage(materialNos, werks);
        log.debug("recive resp:[{}]", test.getStorage(materialNos, werks));

    }

}
