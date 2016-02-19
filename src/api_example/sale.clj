;; defines a compojure web app that is really just a mock for an api
;; service that deals in sale entities.
(ns api-example.sale
  (:require [compojure.core :refer :all]
            [clojure.data.json :as json]))

;; sort of a mock data store of sales
(def ^{:private true} sales 
  [{:id 1
    :productId 7
    :price 10}
   {:id 2
    :productId 8
    :price 2}
   {:id 3
    :productId 5
    :price 99}
   {:id 4
    :productId 8
    :price 1}])
         

(defroutes sale-app
  ;; list all sales
  (GET "/sale" []
       {:status 200
        :headers {"Content-Type" "application/json"}
        :body (json/write-str sales)}))
