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
