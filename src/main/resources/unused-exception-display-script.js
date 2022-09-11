document.addEventListener("htmx:responseError", (detail) => {
    const errorHtml = detail.detail.xhr.responseText
    const windowProperties = {
        width: 1280,
        height: 720,
        left: screen.width / 2 - 1280 / 2,
        top: screen.height / 2 - 720 / 2,
        menubar: "no",
        toolbar: "no",
        location: "no",
        status: "no",
        scrollbars: "yes",
        resizable: "yes",
    }
    const errorWindow = window.open("", "Error",
        Object.entries(windowProperties).map(([key, value]) => key + "=" + value).join(",")
    )
    const parser = new DOMParser()
    const htmlDoc = parser.parseFromString(errorHtml, "text/html")
    const body = htmlDoc.documentElement.querySelector("body")
    body.querySelector("#original-stacktrace").classList.remove("hidden")
    errorWindow.document.write(htmlDoc.documentElement.outerHTML)
})
