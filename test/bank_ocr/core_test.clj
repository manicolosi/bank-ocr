(ns bank-ocr.core-test
  (:require [clojure.test :refer :all]
            [bank-ocr.core :refer :all]))

(deftest recognized-numbers-test
  (is (= (first recognized-numbers) [" _ "
                                     "| |"
                                     "|_|"])))

(deftest parse-test
  (testing "legible"
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
  (testing "illegible"
    (is (= "?23456789"
           (parse ["    _  _     _  _  _  _  _ "
                   "    _| _||_||_ |_   ||_||_|"
                   "  ||_  _|  | _||_|  ||_| _|"
                   "                           "])))))

(deftest validity
  (testing "is valid"
    (is (valid? "345882865"))
    (is (valid? "457508000"))
    (is (valid? "123456789"))
    (is (valid? "000000051")))
  (testing "is not valid"
    (is (not (valid? "664371495")))
    (is (not (valid? "111111111")))))
