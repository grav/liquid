(ns dk.salza.liq.tools.fileutil
  (:require [clojure.string :as str]
            #?(:clj [clojure.java.io :as io])
            [clojure.test :as test]
            #?(:cljs [cljs.nodejs :as node])))

(defn file
  ([folder filename]
   (str (io/file folder filename)))
  ([filepath]
   (str (io/file filepath))))

(defn filename
  [filepath]
  #?(:clj  (str (.getName (io/file filepath)))
     :cljs (.baseName (node/require "path") filepath)))

(defn parent
  [filepath]
  #?(:clj (.getParent (io/file filepath))
     :cljs (let [p (.resolve (node/require "path") filepath "..")]
             (when (not= "/" p)
               p))))
  ;; if root return nil


(defn absolute
  [filepath]
  #?(:clj (.getAbsolutePath (io/file filepath))
     :cljs (.resolve (node/require "path") filepath)))

(defn canonical
  [filepath]
  #?(:clj (.getCanonicalPath (io/file filepath))
     :cljs (absolute filepath)))

(defn folder?
  [filepath]
  #?(:clj  (.isDirectory (io/file filepath))
     :cljs (.isDirectory (.lstatSync (node/require "fs") filepath))))

(defn file?
  [filepath]
  #?(:clj  (.isFile (io/file filepath))
     :cljs (.isFile (.lstatSync (node/require "fs") filepath))))

(defn exists?
  [filepath]
  (.exists (io/file filepath)))

(defn tmp-file
  [filename]
  (str (io/file (System/getProperty "java.io.tmpdir") filename)))

(defn list-files [filepath]
  #?(:clj (.listFiles (io/file filepath))
     :cljs (js->clj (.readdirSync (node/require "fs")))))

(defn get-roots
  []
  #?(:clj (map str (java.io.File/listRoots))
     :cljs (throw (js/Error. "not implemented"))))

(defn get-children
  [filepath]
  #?(:clj (map str (.listFiles (io/file filepath)))
     :cljs (throw (js/Error. "not implemented"))))

(defn get-folders
  [filepath]
  (map str (filter folder? (list-files filepath))))

(defn get-files
  [filepath]
  (filter file? (map str (list-files filepath))))

(defn read-file-to-list
  [filepath]
  #?(:clj (when (file? filepath)
            (with-open [r (io/reader filepath)]
              (rest (apply concat (map #(conj (map str (seq %)) "\n") (doall (line-seq r)))))))
     :cljs (throw (js/Error. "not implemented"))))

(defn write-file
  [filepath content]
  (when (not (folder? filepath))
    #?(:clj (with-open [writer (io/writer filepath)]
              (.write writer content))
       :cljs (.writeFileSync (node/require "fs")
                             filepath
                             content))))
  