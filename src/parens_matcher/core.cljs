(ns parens-matcher.core
  (:require [reagent.core :as r]
            [matchbox.core :as m]
            [matchbox.reagent :as mr]))

(enable-console-print!)

(defonce root (m/connect "https://parens-matcher.firebaseio.com/"))

(defonce matched-parens (mr/sync-rw (m/get-in root [:matched-parens])))

(defonce _init
  (do (m/auth-anon root)
      (when-not @matched-parens (reset! matched-parens 0))))

(defn score []
  [:div.score "(parens-matched " @matched-parens ")"])

(defn parens-button []
  [:div.clickable
    {:on-click #(swap! matched-parens inc)
     :on-mouse-down #(.preventDefault %)}
    [:div.huge-parens
      "( )"]
    [:div.instructions
      "(Click to match parens)"]])

(defn app-root []
  [:div.container
    [score]
    [parens-button]])

(r/render-component [app-root] (.getElementById js/document "app"))
