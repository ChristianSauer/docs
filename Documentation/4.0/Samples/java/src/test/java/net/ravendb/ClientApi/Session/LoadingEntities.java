package net.ravendb.ClientApi.Session;

import net.ravendb.client.documents.DocumentStore;
import net.ravendb.client.documents.IDocumentStore;
import net.ravendb.client.documents.session.IDocumentSession;
import net.ravendb.client.documents.session.loaders.ILoaderWithInclude;

import java.util.Collection;
import java.util.Map;

public class LoadingEntities {
    private interface IFoo {
        //region loading_entities_1_0
        <T> T load(Class<T> clazz, String id);
        //endregion

        //region loading_entities_2_0
        ILoaderWithInclude include(String path);
        //endregion

        //region loading_entities_3_0
        <TResult> Map<String, TResult> load(Class<TResult> clazz, String... ids);

        <TResult> Map<String, TResult> load(Class<TResult> clazz, Collection<String> ids);
        //endregion

        //region loading_entities_4_0
        <T> T[] loadStartingWith(Class<T> clazz, String idPrefix);

        <T> T[] loadStartingWith(Class<T> clazz, String idPrefix, String matches);

        <T> T[] loadStartingWith(Class<T> clazz, String idPrefix, String matches, int start);

        <T> T[] loadStartingWith(Class<T> clazz, String idPrefix, String matches, int start, int pageSize);

        <T> T[] loadStartingWith(Class<T> clazz, String idPrefix, String matches, int start, int pageSize, String exclude);

        <T> T[] loadStartingWith(Class<T> clazz, String idPrefix, String matches, int start, int pageSize, String exclude, String startAfter);
        //endregion

        //region loading_entities_6_0
        boolean isLoaded(String id);
        //endregion
    }

    private static class Employee {

    }
    private static class Supplier {

    }
    private static class Product {
        private String supplier;

        public String getSupplier() {
            return supplier;
        }

        public void setSupplier(String supplier) {
            this.supplier = supplier;
        }
    }

    public LoadingEntities() {
        try (IDocumentStore store = new DocumentStore()) {
            try (IDocumentSession session = store.openSession()) {
                //region loading_entities_1_1
                Employee employee = session.load(Employee.class, "employees/1");
                //endregion
            }

            try (IDocumentSession session = store.openSession()) {
                //region loading_entities_2_1
                // loading 'products/1'
                // including document found in 'supplier' property
                Product product = session
                    .include("supplier")
                    .load(Product.class, "products/1");

                Supplier supplier = session.load(Supplier.class, product.getSupplier()); // this will not make server call
                //endregion
            }

            /* TODO
              using (var session = store.OpenSession())
                {
                    #region loading_entities_2_2

                    // loading 'products/1'
                    // including document found in 'Supplier' property
                    Product product = session
                        .Include<Product>(x => x.Supplier)
                        .Load<Product>("products/1");

                    Supplier supplier = session.Load<Supplier>(product.Supplier); // this will not make server call

                    #endregion
                }
             */
            try (IDocumentSession session = store.openSession()) {
                //region loading_entities_3_1
                Map<String, Employee> employees
                    = session.load(Employee.class,
                    "employees/1", "employees/2", "employees/3");
                //endregion
            }

            try (IDocumentSession session = store.openSession()) {
                //region loading_entities_4_1
                // return up to 128 entities with Id that starts with 'employees'
                Employee[] result = session
                    .advanced()
                    .loadStartingWith(Employee.class, "employees/", null, 0, 128);
                //endregion
            }

            try (IDocumentSession session = store.openSession()) {
                //region loading_entities_4_2
                // return up to 128 entities with Id that starts with 'employees/'
                // and rest of the key begins with "1" or "2" e.g. employees/10, employees/25
                Employee[] result = session
                    .advanced()
                    .loadStartingWith(Employee.class, "employees/", "1*|2*", 0, 128);
                //endregion
            }

            /*
            TODO
             using (var session = store.OpenSession())
                {
                    #region loading_entities_5_1

                    IEnumerator<StreamResult<Employee>> enumerator = session
                        .Advanced
                        .Stream<Employee>("employees/");

                    while (enumerator.MoveNext())
                    {
                        StreamResult<Employee> employee = enumerator.Current;
                    }

                    #endregion
                }

                 using (var session = store.OpenSession())
                {
                    #region loading_entities_5_2

                    using (var outputStream = new MemoryStream())
                    {
                        session
                            .Advanced
                            .LoadStartingWithIntoStream("employees/", outputStream);
                    }

                    #endregion
                }

             */

            try (IDocumentSession session = store.openSession()) {
                //region loading_entities_6_1
                boolean isLoaded = session.advanced().isLoaded("employees/1");//false
                Employee employee = session.load(Employee.class, "employees/1");
                isLoaded = session.advanced().isLoaded("employees/1"); // true
                //endregion
            }
        }
    }
}
