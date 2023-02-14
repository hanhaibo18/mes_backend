package com.richfit.mes.sync;

import com.richfit.mes.common.model.sys.Attachment;
import com.richfit.mes.sync.dao.AttachmentMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author gaol
 * @date 2023/2/14
 * @apiNote
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class MapperTest {

    @Resource
    private AttachmentMapper attachmentMapper;

    @Test
    public void selectOneTest() {
        Attachment attachment = attachmentMapper.selectById("00bbd68eb601de7b605316633869bc57");
        System.out.println(attachment);
    }

    @Test
    public void StringTest() {

        String s = "BOMCO_JM\\AH160101010901\\合格证\\2064010726.jpg";
        s = s.replace("\\", "\\\\");

        System.out.println(s);
    }


    public static void main(String args[]) {

        String s = "BOMCO_JM\\AH160101010901\\合格证\\2064010726.jpg";
        s = s.replace("\\", "\\\\");

        System.out.println(s);

    }

}
