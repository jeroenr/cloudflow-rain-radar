# Rain radar using Cloudflow
This is a sample cloudflow project.

## Deploying on GKE
* Update `target-env.sbt` to match your GCP configuration
* Ensure access to a GKE cluster
```
$ gcloud container clusters get-credentials <cluster-name>
```
* Ensure access to GCR
```
$ gcloud auth configure-docker
```
* Publish to GCR
```
$ sbt buildAndPublish
```
* Deploy the resulting image 
```
$ kubectl-cloudflow deploy -u oauth2accesstoken \
  eu.gcr.io/<gcloud project id>/<image>:<tag> \ 
  -p "$(gcloud auth print-access-token)"
```

## Testing

* Setup port-forwarding to your http ingress
```
$ kubectl -n rain-radar port-forward \
$(kubectl -n rain-radar get po -lcom.lightbend.cloudflow/streamlet-name=http-ingress -o jsonpath="{.items[0].metadata.name}") \
3000:$(kubectl -n rain-radar get po -lcom.lightbend.cloudflow/streamlet-name=http-ingress -o jsonpath="{.items[0].spec.containers[0].ports[0].containerPort}")
```
* Push some data
```$bash
for str in $(cat sample-precipitation-data.json | jq -c '.[]')
do
echo "Using $str"
 curl -i -X POST http://localhost:3000 -H "Content-Type: application/json" --data "$str"
done
```

### Generating test data
Use [json generator](http://json-generator.com)

```$javascript
[
  '{{repeat(10, 20)}}',
  {
    timestamp: '{{integer(1574000000000, 1574973230815)}}',
    location: {
      lat: '{{floating(-90.000001, 90)}}',
      lng: '{{floating(-180.000001, 180)}}',
      city: '{{city()}}'
    },
    value: '{{floating(0, 1)}}'
  }
]
```
