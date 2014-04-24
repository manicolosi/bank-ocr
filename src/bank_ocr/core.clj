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

(defn character-set
  "A character set is a hash where the key is the character and the value is the
  number representing the character."
  [characters]
  (reduce (fn [m [v k]] (assoc m k v)) {}
          (map-indexed vector recognized-numbers)))

(defn parse [input]
  (let [char-set (character-set recognized-numbers)
        parsed-input (separate-numbers 3 3 (take 3 input))]
    (apply str (map char-set parsed-input))))
