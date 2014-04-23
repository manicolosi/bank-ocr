(ns bank-ocr.core-test
  (:require [clojure.test :refer :all]
            [bank-ocr.core :refer :all]))

(deftest recognized-numbers-test
  (testing "0"
    (is (= (first recognized-numbers) [" _ "
                                       "| |"
                                       "|_|"]))))
