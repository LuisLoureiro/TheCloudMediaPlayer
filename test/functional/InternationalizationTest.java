package functional;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.running;
import static play.test.Helpers.testServer;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.fakeApplication;
import static junit.framework.Assert.assertEquals;

import org.junit.Test;

import play.i18n.Lang;
import play.i18n.Messages;
import play.libs.WS;
import play.libs.WS.Response;

public class InternationalizationTest
{
	@Test
	public void testSupportForPortugueseLanguage()
	{
		running(testServer(3333, fakeApplication(inMemoryDatabase())), new Runnable() {
			@Override
			public void run() {
				Response responde = WS.url("http://localhost:3333/auth")
					.setHeader("Accept-Language", "pt")
					.get()
					.get();
				
				// Assert status code
				assertEquals(200, responde.getStatus());
				assertThat(responde.getBody()).contains(Messages.get(new Lang(Lang.forCode("pt")), "authentication.title"));
			}
		});
	}

	@Test
	public void testSupportForEnglishLanguageAndThatEnglishIsTheDefaultLanguage()
	{
		running(testServer(3333, fakeApplication(inMemoryDatabase())), new Runnable() {
			@Override
			public void run() {
				Response responde = WS.url("http://localhost:3333/auth")
					.setHeader("Accept-Language", "en")
					.get()
					.get();
				
				// Assert status code
				assertEquals(200, responde.getStatus());
				assertThat(responde.getBody()).contains(Messages.get(new Lang(Lang.forCode("en")), "authentication.title"));
			}
		});
	}

	@Test
	public void testThatEnglishIsTheDefaultLanguageUsedWhenTheAcceptLanguageHeaderIsNotDefined()
	{
		running(testServer(3333, fakeApplication(inMemoryDatabase())), new Runnable() {
			@Override
			public void run() {
				Response responde = WS.url("http://localhost:3333/auth")
					.setHeader("Accept-Language", "fr")
					.get()
					.get();
				
				// Assert status code
				assertEquals(200, responde.getStatus());
				assertThat(responde.getBody()).contains(Messages.get(new Lang(Lang.forCode("fr")), "authentication.title"));
			}
		});
	}
}
