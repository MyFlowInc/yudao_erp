package cn.iocoder.yudao.framework.banner.core;

import cn.hutool.core.thread.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.util.ClassUtils;

import java.util.concurrent.TimeUnit;

/**
 * 项目启动成功后，提供文档相关的地址
 *
 * @author 芋道源码
 */
@Slf4j
public class BannerApplicationRunner implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) {
        ThreadUtil.execute(() -> {
            ThreadUtil.sleep(1, TimeUnit.SECONDS); // 延迟 1 秒，保证输出到结尾
            log.info("                                                                    \n" +
                                " *            .,,       .,:;;iiiiiiiii;;:,,.     .,,                   \n" +
                                " *          rGB##HS,.;iirrrrriiiiiiiiiirrrrri;,s&##MAS,                \n" +
                                " *         r5s;:r3AH5iiiii;;;;;;;;;;;;;;;;iiirXHGSsiih1,               \n" +
                                " *            .;i;;s91;;;;;;::::::::::::;;;;iS5;;;ii:                  \n" +
                                " *          :rsriii;;r::::::::::::::::::::::;;,;;iiirsi,               \n" +
                                " *       .,iri;;::::;;;;;;::,,,,,,,,,,,,,..,,;;;;;;;;iiri,,.           \n" +
                                " *    ,9BM&,            .,:;;:,,,,,,,,,,,hXA8:            ..,,,.       \n" +
                                " *   ,;&@@#r:;;;;;::::,,.   ,r,,,,,,,,,,iA@@@s,,:::;;;::,,.   .;.      \n" +
                                " *    :ih1iii;;;;;::::;;;;;;;:,,,,,,,,,,;i55r;;;;;;;;;iiirrrr,..       \n" +
                                " *   .ir;;iiiiiiiiii;;;;::::::,,,,,,,:::::,,:;;;iiiiiiiiiiiiri         \n" +
                                " *   iriiiiiiiiiiiiiiii;;;::::::::::::::::;;;iiiiiiiiiiiiiiiir;        \n" +
                                " *  ,riii;;;;;;;;;;;;;:::::::::::::::::::::::;;;;;;;;;;;;;;iiir.       \n" +
                                " *  iri;;;::::,,,,,,,,,,:::::::::::::::::::::::::,::,,::::;;iir:       \n" +
                                " * .rii;;::::,,,,,,,,,,,,:::::::::::::::::,,,,,,,,,,,,,::::;;iri       \n" +
                                " * ,rii;;;::,,,,,,,,,,,,,:::::::::::,:::::,,,,,,,,,,,,,:::;;;iir.      \n" +
                                " * ,rii;;i::,,,,,,,,,,,,,:::::::::::::::::,,,,,,,,,,,,,,::i;;iir.      \n" +
                                " * ,rii;;r::,,,,,,,,,,,,,:,:::::,:,:::::::,,,,,,,,,,,,,::;r;;iir.      \n" +
                                " * .rii;;rr,:,,,,,,,,,,,,,,:::::::::::::::,,,,,,,,,,,,,:,si;;iri       \n" +
                                " *  ;rii;:1i,,,,,,,,,,,,,,,,,,:::::::::,,,,,,,,,,,,,,,:,ss:;iir:       \n" +
                                " *  .rii;;;5r,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,sh:;;iri        \n" +
                                " *   ;rii;:;51,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,.:hh:;;iir,        \n" +
                                " *    irii;::hSr,.,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,.,sSs:;;iir:         \n" +
                                " *     irii;;:iSSs:.,,,,,,,,,,,,,,,,,,,,,,,,,,,..:135;:;;iir:          \n" +
                                " *      ;rii;;:,r535r:...,,,,,,,,,,,,,,,,,,..,;sS35i,;;iirr:           \n" +
                                " *       :rrii;;:,;1S3Shs;:,............,:is533Ss:,;;;iiri,            \n" +
                                " *        .;rrii;;;:,;rhS393S55hh11hh5S3393Shr:,:;;;iirr:              \n" +
                                " *          .;rriii;;;::,:;is1h555555h1si;:,::;;;iirri:.               \n" +
                                " *            .:irrrii;;;;;:::,,,,,,,,:::;;;;iiirrr;,                  \n" +
                                " *               .:irrrriiiiii;;;;;;;;iiiiiirrrr;,.                    \n" +
                                " *                  .,:;iirrrrrrrrrrrrrrrrri;:.                        \n" +
                                " *                        ..,:::;;;;:::,,.                             \n" +
                    " */ \n" );

            // 数据报表
            if (isNotPresent("cn.iocoder.yudao.module.report.framework.security.config.SecurityConfiguration")) {
                System.out.println("[报表模块 yudao-module-report - 已禁用][参考 https://doc.iocoder.cn/report/ 开启]");
            }
            // 工作流
            if (isNotPresent("cn.iocoder.yudao.module.bpm.framework.flowable.config.BpmFlowableConfiguration")) {
                System.out.println("[工作流模块 yudao-module-bpm - 已禁用][参考 https://doc.iocoder.cn/bpm/ 开启]");
            }
            // ERP 系统
            if (isNotPresent("cn.iocoder.yudao.module.erp.framework.web.config.ErpWebConfiguration")) {
                System.out.println("[ERP 系统 yudao-module-erp - 已禁用][参考 https://doc.iocoder.cn/erp/build/ 开启]");
            }
            // CRM 系统
            if (isNotPresent("cn.iocoder.yudao.module.crm.framework.web.config.CrmWebConfiguration")) {
                System.out.println("[CRM 系统 yudao-module-crm - 已禁用][参考 https://doc.iocoder.cn/crm/build/ 开启]");
            }
        });
    }

    private static boolean isNotPresent(String className) {
        return !ClassUtils.isPresent(className, ClassUtils.getDefaultClassLoader());
    }

}
