;; defines a compojure web app that is really just a mock for an api
;; service that deals in product entities.
(ns api-example.product
  (:require [compojure.core :refer :all]
            [clojure.data.json :as json]))

;; our mock data store of products
(def ^{:private true} products
  [{:id 1
    :color :blue
    :price 20}
   {:id 2
    :color :green
    :price 5}
   {:id 3
    :color :puce
    :price 20}
   {:id 4
    :color :chartreuse
    :price 10}
   {:id 5
    :color :taupe
    :price 199}
   {:id 6
    :color :red
    :price 19}
   {:id 7
    :color :orange
    :price 20}
   {:id 8
    :color :yellow
    :price 4}
   {:id 9
    :color :black
    :price 8}
   {:id 10
    :color :taupe
    :price 2999}])

(defroutes product-app
  ;; list all products

  (GET "/product" []
    {:status 200
     :headers {"Content-Type" "application/json"}
     :body (json/write-str products)}))
