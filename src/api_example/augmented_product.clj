;; compojure web app that provides a composite service api: lists products
;; from the product-api but makes sure to display sale prices from the sale api
;; if any exist, rather than the default product price
(ns api-example.augmented-product
  (:require [compojure.core :refer :all]
            [clojure.data.json :as json]
            [org.httpkit.client :as http]))

(def host (if-let [host (System/getenv "HOST")]
            host
            "localhost"))

(def product-port "7980")
(def product-resource "product")

(def sale-port "7981")
(def sale-resource "sale")

;; this would probably be an overly specific abstraction in a real-world system
;; but it's DRY here...
(defn- list-remote [host port resource]
  (let [url
          (str "http://" host ":" port "/" resource)
        _
          (println "making GET request to: " url)
        {:keys [status headers body error] :as resp}
          @(http/get url)]
    (if error
      (println "Error retrieving " resource " list from url: " url ": " error)
      (json/read-str body :key-fn keyword))))

(defn- list-products []
  (list-remote host product-port product-resource))

(defn- list-sales []
  (list-remote host sale-port sale-resource))

;; In reality, if we had a lot of sales, we might consider caching this stuff
;; locally. Of course, then you have to worry about how best to invalidate your
;; cache when the sale data changes...
;; We might also use some kind of tree or hashtable to make this not O(n) if we are calling
;; this for many products.
;; Of course, the best solution to both problems would be to have the sale service provide a 
;; filter or endpoint for getting sales just for a given product-id.
(defn- get-sales-by-product-id [sales product-id]
  (filter #(= (:productId %) product-id) sales))

;; find the lowest price for a product and all it's associated sales
(defn- lowest-price [product sales]
  (reduce (fn [cur-lowest sale] 
            (if (< (:price sale) cur-lowest)
              (:price sale)
              cur-lowest))
          (:price product)
          sales))

;; open question: if a single product id has more than one sale what's the best way to 
;; pick the price returned to the user? I'm picking the lowest, but we may want to 
;; consider other criteria
(defn- augment-products[]
  (let [products (future (list-products))
        sales    (future (list-sales))
        sales-for-products (map (fn [product] 
                                  (get-sales-by-product-id @sales (:id product))) @products)]
    ;; map over each product and its associated sales picking the lowest price
    ;; and returning the product data with that price.
    (map (fn [product sales] (assoc product :price (lowest-price product sales)))
         @products
         sales-for-products)))

(defroutes augmented-product-app
  ;; list products with applicable sale prices
  (GET "/product" []
    {:status 200
     :headers {"Content-Type" "application/json"}
     :body (json/write-str (augment-products))}))
