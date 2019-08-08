package mappertest;

import com.github.pagehelper.PageHelper;
import com.macro.mall.portal.MallFoodPortalApplication;
import com.macro.mall.portal.dao.PortalOrderDao;
import com.macro.mall.portal.domain.OmsMemberOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.lang.reflect.Proxy;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MallFoodPortalApplication.class)
@EnableAutoConfiguration
public class RunTests {
    @Resource
    private PortalOrderDao portalOrderDao;
    Long kk;

    @Test
    public void contextLoads() {

        System.out.println(kk);
//        PageHelper.startPage(1, 5);
        List<OmsMemberOrder> list = portalOrderDao.getDetailsBymember(1l,5,2);
        System.out.println(list.size());
    }
}
