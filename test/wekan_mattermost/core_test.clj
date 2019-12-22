(ns wekan-mattermost.core_test
  (:require [wekan-mattermost.core :as core]
            [clojure.test :refer [deftest is]]))

(deftest right-text
  (let [text "Afgan0r created card \"interest card\" to list \"22\" at swimlane \"Default\" at board \"test\"\nhttp://localhost/b/5wBEbxeMzdm25eHqb/test/oTGtjqKcKPjWEdZKw"
        resultText {:text "Afgan0r created card \"[interest card](http://localhost/b/5wBEbxeMzdm25eHqb/test/oTGtjqKcKPjWEdZKw)\" to list \"22\" at swimlane \"Default\" at board \"test\""}
        card "interest card"
        cardId "oTGtjqKcKPjWEdZKw"
        boardId "5wBEbxeMzdm25eHqb"]
    (is (= resultText (core/wekan->mattermost {:text text :card card :cardId cardId :boardId boardId})))))

(deftest wrong-url-in-text
  (let [text "Afgan0r created card \"interest card\" to list \"22\" at swimlane \"Default\" at board \"test\"\nhttp://localhost/5wBEbxeMzdm25eHqb/test"
        resultText {:text "Afgan0r created card \"interest card\" to list \"22\" at swimlane \"Default\" at board \"test\""}
        card "interest card"
        cardId "oTGtjqKcKPjWEdZKw"
        boardId "5wBEbxeMzdm25eHqb"]
    (is (= resultText (core/wekan->mattermost {:text text :card card :cardId cardId :boardId boardId})))))

(deftest wrong-url-in-text-and-without-card
  (let [text "Afgan0r created card \"interest card\" to list \"22\" at swimlane \"Default\" at board \"test\"\nhttp://localhost/5wBEbxeMzdm25eHqb/test"
        resultText {:text "Afgan0r created card \"interest card\" to list \"22\" at swimlane \"Default\" at board \"test\""}
        card nil
        cardId nil
        boardId nil]
    (is (= resultText (core/wekan->mattermost {:text text :card card :cardId cardId :boardId boardId})))))

(deftest text-without-url
  (let [text "Afgan0r created card \"interest card\" to list \"22\" at swimlane \"Default\" at board \"test\""
        resultText {:text "Afgan0r created card \"interest card\" to list \"22\" at swimlane \"Default\" at board \"test\""}
        card "interest card"
        cardId "oTGtjqKcKPjWEdZKw"
        boardId "5wBEbxeMzdm25eHqb"]
    (is (= resultText (core/wekan->mattermost {:text text :card card :cardId cardId :boardId boardId})))))

(deftest text-without-url-and-card
  (let [text "Afgan0r created card \"interest card\" to list \"22\" at swimlane \"Default\" at board \"test\""
        resultText {:text "Afgan0r created card \"interest card\" to list \"22\" at swimlane \"Default\" at board \"test\""}
        card nil
        cardId nil
        boardId nil]
    (is (= resultText (core/wekan->mattermost {:text text :card card :cardId cardId :boardId boardId})))))

(deftest added-new-list-text
  (let [text "Afgan0r added list \"New list name\" to board \"test\"\nhttp://localhost/b/5wBEbxeMzdm25eHqb/test"
        resultText {:text "Afgan0r added list \"New list name\" to board \"test\""}
        card nil
        cardId nil
        boardId "5wBEbxeMzdm25eHqb"]
    (is (= resultText (core/wekan->mattermost {:text text :card card :cardId cardId :boardId boardId})))))