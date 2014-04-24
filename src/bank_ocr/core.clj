(ns bank-ocr.core)

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

(defn character-set
  "A character set is a hash where the key is the character and the value is the
  number representing the character."
  [characters]
  (hash-map-with-index-values recognized-numbers))

(defn parse [input]
  (let [char-set (character-set recognized-numbers)
        parsed-input (separate-numbers 3 3 (take 3 input))]
    (apply str (map #(get char-set %1 "?") parsed-input))))

(defn int-seq
  [n]
  (map #(Integer/parseInt (str %1)) (str n)))

(defn valid?
  "Checksums a bank account number returns a truthy value if it is valid."
  [number]
  (letfn [(checksum []
            (->> (int-seq number)
                 reverse
                 (map-indexed (fn [i n] (* (inc i) n)))
                 (reduce +)))]
    (= (mod (checksum) 11) 0)))
