(ns parens-matcher.core
  (:require [reagent.core :as r]))

(enable-console-print!)

(defonce matched-parens (r/atom 0))

(defn score []
  [:div.score "(parens-matched " @matched-parens ")"])

(defn parens-button []
  [:div
    {:on-click #(swap! matched-parens inc)}
    [:div.huge-parens
      "( )"]
    [:div.instructions
      "(Click to match parens)"]])

(defn app-root []
  [:div.container
    [score]
    [parens-button]])

(r/render-component [app-root] (.getElementById js/document "app"))
