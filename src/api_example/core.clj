(ns api-example.core
  (:require [compojure.core :refer :all]
            [org.httpkit.server :refer [run-server]]
            [api-example.sale :as sale]
            [api-example.product :as product]
            [api-example.augmented-product :as augmented-product]))

(defn -main []
 (println "starting webapps")
  (run-server augmented-product/augmented-product-app {:port 7979})
  (run-server product/product-app {:port 7980})
  (run-server sale/sale-app {:port 7981})
  (println "webapps running!"))

