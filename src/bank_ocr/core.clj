(ns bank-ocr.core
  (:require [clojure.java.io :as io])
  (:gen-class :main true))

(defn partition-string [n s]
  (map (partial apply str) (partition n s)))

(defn separate-numbers
  "Takes a sequence of strings representing lines of a character set and
  re-arranges them into individual characters."
  [char-width char-height lines]
  (->> lines
       (map (partial partition-string char-width))
       (apply interleave)
       (partition char-height)))

(def recognized-numbers
  (separate-numbers 3 3
    [" _     _  _     _  _  _  _  _ "
     "| |  | _| _||_||_ |_   ||_||_|"
     "|_|  ||_  _|  | _||_|  ||_| _|"]))

(defn hash-map-with-index-values
  [a-seq]
  (reduce (fn [m [v k]] (assoc m k v)) {}
          (map-indexed vector a-seq)))

(defn parse [input]
  (let [char-set (hash-map-with-index-values recognized-numbers)
        parsed-input (separate-numbers 3 3 (take 3 input))]
    (apply str (map #(get char-set %1 "?") parsed-input))))

(defn int-seq
  [n]
  (map #(Integer/parseInt (str %1)) (str n)))

(defn valid?
  "Checksums a parsed bank account number returns a truthy value if it is
  valid."
  [number]
  (let [checksum (->> (int-seq number)
                      reverse
                      (map-indexed (fn [i n] (* (inc i) n)))
                      (reduce +))]
    (zero? (mod checksum 11))))

(defn legible?
  "A parsed bank account number is legible if it doesn't contain any question
  marks."
  [number]
  (not (some #{\?} number)))

(defn write-account-number [writer account]
  (.write writer account)
  (.write writer
    (cond
      (not (legible? account)) " ILL"
      (not (valid? account)) " ERR"
      :else ""))
  (.write writer "\n"))

(defn process [filename]
  (with-open [reader (io/reader filename)
              writer (io/writer (str filename ".out"))]
    (doseq [lines (partition 4 (line-seq reader))
            :let [account (parse lines)]]
      (write-account-number writer account))))

(defn -main [& args]
  (doseq [filename args]
    (process filename)))
