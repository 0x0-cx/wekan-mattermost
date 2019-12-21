(ns wekan-mattermost.test
  (:require [wekan-mattermost.core :as core]
            [clojure.test :refer [deftest is]]))

(deftest right-text
  ; right text
  (let [in_text "Afgan0r created card \"interest card\" to list \"22\" at swimlane \"Default\" at board \"test\"\nhttp://localhost/b/5wBEbxeMzdm25eHqb/test/oTGtjqKcKPjWEdZKw"
        result_text {:text "Afgan0r created card \"[interest card](http://localhost/b/5wBEbxeMzdm25eHqb/test/oTGtjqKcKPjWEdZKw)\" to list \"22\" at swimlane \"Default\" at board \"test\""}
        card "interest card"
        cardId "oTGtjqKcKPjWEdZKw"
        boardId "5wBEbxeMzdm25eHqb"]
    (is (= result_text (core/wekan->mattermost {:text in_text :card card :cardId cardId :boardId boardId})))))

(deftest wrong-url-in-text
  ; wrong URL
  (let [in_text "Afgan0r created card \"interest card\" to list \"22\" at swimlane \"Default\" at board \"test\"\nhttp://localhost/5wBEbxeMzdm25eHqb/test"
        result_text {:text "Afgan0r created card \"interest card\" to list \"22\" at swimlane \"Default\" at board \"test\""}
        card "interest card"
        cardId "oTGtjqKcKPjWEdZKw"
        boardId "5wBEbxeMzdm25eHqb"]
    ; TODO Fix this case
    (is (= result_text (core/wekan->mattermost {:text in_text :card card :cardId cardId :boardId boardId})))))

(deftest text-without-url
  ; text with no URL
  (let [in_text "Afgan0r created card \"interest card\" to list \"22\" at swimlane \"Default\" at board \"test\""
        result_text {:text "Afgan0r created card \"interest card\" to list \"22\" at swimlane \"Default\" at board \"test\""}
        card "interest card"
        cardId "oTGtjqKcKPjWEdZKw"
        boardId "5wBEbxeMzdm25eHqb"]
    (is (= result_text (core/wekan->mattermost {:text in_text :card card :cardId cardId :boardId boardId})))))

(deftest added-new-list-text
  ; text when added new list
  (let [in_text "Afgan0r added list \"New list name\" to board \"test\"\nhttp://localhost/b/5wBEbxeMzdm25eHqb/test"
        result_text {:text "Afgan0r added list \"New list name\" to board \"(test)[http://localhost/b/5wBEbxeMzdm25eHqb/test]\""}
        card nil
        cardId nil
        boardId "5wBEbxeMzdm25eHqb"]
    ; TODO Fix this case
    (is (= result_text (core/wekan->mattermost {:text in_text :card card :cardId cardId :boardId boardId})))))