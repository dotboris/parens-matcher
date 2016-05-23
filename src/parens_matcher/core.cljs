(ns parens-matcher.core
  (:require [reagent.core :as r]
            [matchbox.core :as m]
            [matchbox.reagent :as mr]))

(enable-console-print!)

(defonce root (m/connect "https://parens-matcher.firebaseio.com/"))
(defonce matched-parens (mr/sync-rw (m/get-in root [:matched-parens])))
(defonce levels (mr/sync-rw (m/get-in root [:levels])))
(defonce _init (m/auth-anon root))

(def cost-per-level
  {:foobar 10
   :bizbaz 100
   :qixqux 1000})

(defn level [upgrade]
  (-> @levels
      upgrade
      (or 0)))

(defn next-level [upgrade]
  (inc (level upgrade)))

(defn upgrade-cost [upgrade]
  (* (next-level upgrade)
     (cost-per-level upgrade)))

(defn can-upgrade? [upgrade]
  (>= (or @matched-parens 0)
      (upgrade-cost upgrade)))

(defn buy! [upgrade]
  (when (can-upgrade? upgrade)
    (swap! matched-parens - (upgrade-cost upgrade))
    (swap! levels update-in [upgrade] inc)))

(defn score []
  [:div.score "(parens-matched " (or @matched-parens 0) ")"])

(defn parens-button []
  [:div.clickable
    {:on-click #(swap! matched-parens inc)
     :on-mouse-down #(.preventDefault %)}
    [:div.huge-parens
      "( )"]
    [:div.instructions
      "(Click to match parens)"]])

(defn indent []
  [:span {:dangerouslySetInnerHTML {:__html "&nbsp;&nbsp;"}}])

(defn upgrade-button [title key]
  [:button.btn.btn-default.btn-lg.btn-block.upgrade-button
    {:class (when-not (can-upgrade? key) "disabled")
     :on-click #(buy! key)}
    [:div "(" title]
    [:div [indent] ":level " (level key)]
    [:div [indent] ":upgrade-cost " (upgrade-cost key) ")"]])

(defn upgrade-buttons []
  [:div
    [upgrade-button "foobar" :foobar]
    [upgrade-button "bizbaz" :bizbaz]
    [upgrade-button "qixqux" :qixqux]])

(defn app-root []
  [:div.container
    [score]
    [:div.row
      [:div.col-sm-8 [parens-button]]
      [:div.col-sm-4 [upgrade-buttons]]]])

(r/render-component [app-root] (.getElementById js/document "app"))
