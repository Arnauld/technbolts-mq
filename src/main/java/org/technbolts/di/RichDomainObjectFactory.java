package org.technbolts.di;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

/**
 * By declaring this RichDomainObjectFactory as a Spring bean,
 * the Spring Container makes sure that <code>AutowireCapableBeanFactory</code>
 * is set when the container is initialized:
 *
 * &lt;bean class="org.jsi.di.spring.RichDomainObjectFactory" factory-method="autoWireFactory"/&gt;
 *
 * Instead of letting the Spring Container create its own instance of
 * a <code>RichDomainObjectFactory</code>, the factory-method attribute
 * is used in the bean definition, which forces Spring to use the
 * reference returned by the autoWireFactory() method, which is the
 * singleton itself. By doing so, the singleton instance of the
 * <code>RichDomainObjectFactory</code> gets the
 * <code>AutowireCapableBeanFactory</code> injected. Since a singleton
 * can be accessed from everywhere in the same classloader scope, every
 * class in that scope can make use of the RichDomainObjectFactory,
 * which exposes the autowire features of Spring in a non-intrusive,
 * loosely coupled manner. Needless to say that Scala code can access
 * the RichDomainObjectFactory singleton as well and use its autowire
 * function.
 *
 * <a href="http://www.infoq.com/articles/scala_and_spring">
 *  Scala &amp; Spring: Combine the best of both worlds</a>
 *
 */
public class RichDomainObjectFactory  implements BeanFactoryAware {

    private static final Log LOG = LogFactory.getLog(RichDomainObjectFactory.class);

    private AutowireCapableBeanFactory factory = null;

    public <T> T createAndAutowire(Class<T> clazz) {
        T instance = null;
        try {
            instance = clazz.newInstance();
            autowire(instance);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return instance;
    }

    public <T> T autowire(T instance) {
        if (factory != null) {
            factory.autowireBeanProperties(instance, AutowireCapableBeanFactory.AUTOWIRE_BY_NAME, false);
        } else {
            LOG.warn("No " + AutowireCapableBeanFactory.class.getName() + " has been defined. Autoworing will not work.");
        }
        return instance;
    }

    public void setBeanFactory(BeanFactory factory) throws BeansException {
        this.factory = (AutowireCapableBeanFactory) factory;
    }

    private static RichDomainObjectFactory singleton = new RichDomainObjectFactory();

    public static RichDomainObjectFactory autoWireFactory() {
        return singleton;
    }

    public static void setInstance(RichDomainObjectFactory richDomainObjectFactory) {
        singleton = richDomainObjectFactory;
    }
}