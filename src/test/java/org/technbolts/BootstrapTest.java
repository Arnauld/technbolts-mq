package org.technbolts;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.technbolts.integration.rss.RssFetcher;

@ContextConfiguration(locations={"classpath:applicationContext.xml"})
@Ignore
public class BootstrapTest extends AbstractJUnit4SpringContextTests {
	
	@Autowired
	private RssFetcher rssReader;
	
	@Test
	public void testEx1 () throws InterruptedException {
		assertThat(rssReader, notNullValue());
		Thread.sleep(20000);
		System.out.println("Done!");
	}

}
