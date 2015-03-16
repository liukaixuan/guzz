### New Architecture: MMVC over the old MVC ###

MMVC = (Traditional) Model + (Smart) Model + View + Controller.

Look at the picture below:

![http://guzz.org/wiki-images/mmvc_architecture.png](http://guzz.org/wiki-images/mmvc_architecture.png)

We add a "smart model" in the View layer. It is declarations for the data required.

In the traditional MVC architecture, View layer is just a simple template. The data is decided by the Model View. But this is wrong, it reverses the fact, as the View layer is the only place knows exactly which data is needed, not the model (maybe your java logic) at all.

The problem of MVC is **WE** have to tell the model which data the view needs, so human logic is a must for every view pages, old or new. We, the human has to foresee all data the view page may need in the future, that is impossible! The question is, why can't the View determine what she needs herself? Or, why cann't the View tell the model what she needs herself?

This is the key of MMVC, a smart model is introduced to satisfy the View's demands.

The smart model would be clever enough to load data for the View, and in the meantime, doesn't mess up java codes with template codes to send us to the old dark times. So, we need the View to tell what she needs in a clean way, by declarations.

The declarations can be done in the container or in the template file(as my picture shows). In JSP, we can use taglib, in velocity, custom director is a nice choice too.

In this architecture, global, user-inputed and context related data is passed to the View as usual as they are runtime data, but "feature" related data which the view page itself knows is declared and loaded as the declared data, such as the latest 10 news of the passed channel. A good controller combines the two sources together, and walks down as the traditional MVC does to generate the returned result.

This is MMVC!

In the end, I must clarify that MMVC doesn't break the separation of java logic and template pages. Rather, it moves your "feature data java logic" written in every projects to a clever Controller, or framework, or a container, and leaves you another easier way to just declare for it. Maybe it's a new choice.