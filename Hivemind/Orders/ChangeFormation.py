from Orders.Order import Order

class ChangeFormationOrder(Order):
    def __init__(self, formation):
        self.formation = formation
    def execute(self, squadron):
        squadron.set_formation(self.formation)
