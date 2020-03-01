(ns ^:figwheel-hooks cliche.core
  (:require
    [goog.dom :as gdom]
    [cljsjs.medium-editor]
    [cljs.pprint :as pp]
    [reagent.core :as r :refer [atom]]))

;; Just a dummy function to check that testing setup is ok.
(defn multiply [a b] (* a b))

;; The stateful program data that changes as we edit.
(defonce app-state
         (atom {:default-text "Document state will appear here."
                :doc-html     "<p>
                              <big><b>Here's</b></big>
                              <strike>some</strike> <u>formatted</u> <i>text</i>
                              to <span style=\"color:red;\">start</span> with.
                              </p>
                              <p>
                              And <a href=\"https://github.com/clartaq/cliche\">
                              here's</a> the project repository.
                              </p>"}))


;; Ids for some of the elements in the page.

(def text-output-div-id "text-output")
(def html-output-div-id "html-output")
(def doc-state-div-id "doc-state-output")
(def ed-settings {:editor-div-id    "the-editor"
                  :editor-div-class "cliche-editor"})

;; Demo utilities.

(defn id->ele [id]
  "Return the page element with the given id."
  (gdom/getElement (name id)))

(defn add-html!
  [id html]
  (set! (.-innerHTML (id->ele id)) html))

(defn add-text!
  [id txt]
  (set! (.-textContent (id->ele id)) txt))

(defn add-state!
  [id]
  (if (empty? (:doc-html @app-state))
    (set! (.-textContent (id->ele id)) (:default-text @app-state))
    (set! (.-textContent (id->ele id)) @app-state)))

;; Layout the app.

(defn layout-medium-editor
  "Return a rich text editor component."
  [asr settings]
  (let [opts (clj->js
               {:buttonLabels "fontawesome"
                :spellcheck   "false"
                :placeholder  {:text "Type your text here"}
                :toolbar      {:buttons
                               ["bold", "italic", "underline",
                                "strikethrough", "quote", "anchor",
                                "image", "justifyLeft", "justifyCenter",
                                "justifyRight", "justifyFull",
                                "superscript", "subscript",
                                "orderedlist", "unorderedlist",
                                "pre", "outdent", "indent", "h2", "h3"]}})
        editor-atom (atom nil)
        handle-editor-change (fn [_ _]
                               (let [new-content (.getContent @editor-atom)]
                                 (swap! asr assoc :doc-html new-content)
                                 (add-html! text-output-div-id new-content)
                                 (add-text! html-output-div-id new-content)
                                 (add-state! doc-state-div-id)))]
    (r/create-class
      {:display-name           "cliche-medium-editor"

       :component-did-mount    (fn []
                                 (let [ele (.getElementById js/document (:editor-div-id settings))]
                                   (reset! editor-atom (js/MediumEditor. ele opts))
                                   (.subscribe @editor-atom "editableInput" handle-editor-change)
                                   (if-let [initial-html (:doc-html @asr)]
                                     (do
                                       (add-html! text-output-div-id initial-html)
                                       (add-text! html-output-div-id initial-html)
                                       (add-state! doc-state-div-id)
                                       (.setContent @editor-atom initial-html))
                                     (.setContent @editor-atom "<p><b>What?</b> No Content?</p>" 0))
                                   (.focus ele)))

       :component-will-unmount (fn []
                                 (.destroy @editor-atom)
                                 (reset! editor-atom nil))

       :component-did-update   (fn [_ _ _]
                                 (.restoreSelection @editor-atom))

       :reagent-render         (fn []
                                 [:div {:class (:editor-div-class settings)
                                        :id    (:editor-div-id settings)}])})))

(defn demo-page
  "Build and return the demo page markup."
  [asr]
  [:main
   [:header
    [:h2 "Welcome to Cliche"]
    [:h3 "A " [:span [:code "medium-editor"]] "-based Rich Text Editor written in ClojureScript and Reagent"]
    [:h4 "This is just a demo. It doesn't store your changes anywhere."]]
   [:div {:class "two-column-div"}
    [:div {:class "editor-column"}
     [layout-medium-editor asr ed-settings]]
    [:div {:class "info-column"}
     [:div {:class "info-item"}
      [:h4 {:class "info-title"} "Text output:"]
      [:div {:id text-output-div-id :class "info-text-data"}]]
     [:div {:class "info-item"}
      [:h4 {:class "info-title"} "HTML output:"]
      [:div {:id html-output-div-id :class "info-code-data"}]]
     [:div {:class "info-item"}
      [:h4 {:class "info-title"} "Document state:"]
      [:div {:id doc-state-div-id :class "info-code-data"}]]]]])

;;; Initialize the app.

(defn mount-root
  "Start 'er up!"
  []
  (r/render [demo-page app-state] (id->ele "app"))
  (add-text! text-output-div-id "Formatted text will appear here.")
  (add-html! html-output-div-id "Raw HTML will appear here.")
  (add-state! doc-state-div-id))

;; Conditionally start your application based on the presence of an "app"
;; element. This is particularly helpful for testing this ns without launching
;; the app
(mount-root)

;; Specify reload hook with ^;after-load metadata.
(defn ^:after-load on-reload []
  (mount-root)
  ;; Optionally touch your app-state to force rerendering depending on
  ;; your application:
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  )
