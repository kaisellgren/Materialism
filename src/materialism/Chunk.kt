package materialism

import java.util.*

class Chunk {
    val models: ArrayList<Model> = arrayListOf()

    fun delete() {
        for (model in models) {
            model.delete()
        }
    }
}
