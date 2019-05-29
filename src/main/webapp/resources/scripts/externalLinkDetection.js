function makeExternal(link) {
    let url = link.getAttribute('href');
    if (url.includes("//")) {
        link.classList.add("external")
    }
}

for (var l = 0, links = document.querySelectorAll('a'), ll = links.length; l < ll; ++l ){
    makeExternal(links[l]);
}