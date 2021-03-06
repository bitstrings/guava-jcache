/*
 * Copyright 2016 ExProfesso.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ca.exprofesso.guava.jcache;

import static org.junit.Assert.*;

import java.util.Properties;

import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.spi.CachingProvider;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class GuavaCacheManagerTest
{
    private CachingProvider cachingProvider;

    @Before
    public void init()
    {
        cachingProvider = Caching.getCachingProvider(GuavaCachingProvider.class.getName());

        assertNotNull(cachingProvider);
    }

    @After
    public void close()
    {
        cachingProvider.close();

        cachingProvider = null;
    }

    @Test
    public void testCacheManager()
        throws Exception
    {
        CacheManager cacheManager = cachingProvider.getCacheManager();

        assertEquals(cacheManager, cachingProvider.getCacheManager());

        cacheManager.close();

        assertTrue(cacheManager.isClosed());
    }

    @Test
    public void testCacheManagerWithCustomClassLoader()
        throws Exception
    {
        CacheManager cacheManager1 = cachingProvider.getCacheManager();
        CacheManager cacheManager2 = cachingProvider.getCacheManager(null, ClassLoader.getSystemClassLoader());

        assertNotNull(cacheManager1);
        assertNotNull(cacheManager2);

        assertNotEquals(cacheManager1, cacheManager2);

        cacheManager1.close();
        cacheManager2.close();

        assertTrue(cacheManager1.isClosed());
        assertTrue(cacheManager2.isClosed());
    }

    @Test
    public void testCacheManagerWithCustomProperties()
        throws Exception
    {
        Properties properties = new Properties();

        properties.setProperty("concurrencyLevel", "1");
        properties.setProperty("initialCapacity", "16");

        CacheManager cacheManager1 = cachingProvider.getCacheManager();
        CacheManager cacheManager2 = cachingProvider.getCacheManager(null, null, properties);

        assertNotNull(cacheManager1);
        assertNotNull(cacheManager2);

        assertNotEquals(cacheManager1, cacheManager2);

        cacheManager1.close();
        cacheManager2.close();

        assertTrue(cacheManager1.isClosed());
        assertTrue(cacheManager2.isClosed());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCacheManagerWithCustomInvalidProperties()
        throws Exception
    {
        Properties properties = new Properties();

        properties.setProperty("unknownProperty", "true");

        try (CacheManager cacheManager = cachingProvider.getCacheManager(null, null, properties);)
        {
            assertNotNull(cacheManager);

            cacheManager.createCache("test", new MutableConfiguration());
        }
    }

    @Test
    public void testCacheManagerWithCustomClassLoaderAndProperties()
        throws Exception
    {
        Properties properties = new Properties();

        properties.setProperty("concurrencyLevel", "1");
        properties.setProperty("initialCapacity", "16");

        CacheManager cacheManager1 = cachingProvider.getCacheManager();
        CacheManager cacheManager2 =
            cachingProvider.getCacheManager(null, ClassLoader.getSystemClassLoader(), properties);

        assertNotNull(cacheManager1);
        assertNotNull(cacheManager2);

        assertNotEquals(cacheManager1, cacheManager2);

        cacheManager1.close();
        cacheManager2.close();

        assertTrue(cacheManager1.isClosed());
        assertTrue(cacheManager2.isClosed());
    }

    @Test(expected = IllegalStateException.class)
    public void testClosedCacheManager()
        throws Exception
    {
        CacheManager cacheManager = cachingProvider.getCacheManager();

        assertNotNull(cacheManager);

        cacheManager.close();

        cacheManager.getCacheNames();
    }
}
