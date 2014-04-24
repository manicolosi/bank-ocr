(ns bank-ocr.core-test
  (:require [clojure.test :refer :all]
            [bank-ocr.core :refer :all]))

(deftest recognized-numbers-test
  (is (= (first recognized-numbers) [" _ "
                                     "| |"
                                     "|_|"])))

(deftest parse-test
  (is (= "123456789"
         (parse ["    _  _     _  _  _  _  _ "
                 "  | _| _||_||_ |_   ||_||_|"
                 "  ||_  _|  | _||_|  ||_| _|"
                 "                           "])))
  (is (= "000000051"
         (parse [" _  _  _  _  _  _  _  _    "
                 "| || || || || || || ||_   |"
                 "|_||_||_||_||_||_||_| _|  |"
                 "                           "]))))
