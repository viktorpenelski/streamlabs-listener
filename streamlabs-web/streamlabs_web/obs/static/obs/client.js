// sample request: http://localhost:8000/api/obs/aggregate?tag=%23foo&tag=%23bar&min_date=2020-12-20T17%3A18%3A21%2B02%3A00&max_date=2020-12-24T17%3A20%3A21%2B02%3A00
// sample response (as promise):
// {
//     "aggregated_tags": [{
//         "tag": "#foo",
//         "amount": 42.42
//     }, {
//         "tag": "#bar",
//         "amount": 0.0
//     }]
// }
function fetchData(tags, minDate, maxDate) {
    let url = new URL(window.location.origin + '/api/obs/aggregate');
    tags.forEach(tag => url.searchParams.append("tag", tag));
    url.searchParams.append("min_date", minDate);
    url.searchParams.append("max_date", maxDate);
    return fetch(url.toString()).then(r => r.json())
}

// sample request: http://localhost:8000/api/obs/aggregate_slugs/{slug}/
// sample response (as promise)
// {
//      max_date: "2020-12-24T17:20:21+02:00"
//      min_date: "2020-12-20T17:18:21+02:00"
//      slug: "streampc"
//      tags: "#foo\n#bar\n#rakavici"
// }
function fetchMetadataFor(slug) {
    return fetch(window.location.origin + '/api/obs/aggregate_slugs/' + slug + "/").then(r => r.json());
}
